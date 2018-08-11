package pro.taskana.camunda.scheduler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.TimeInterval;
import pro.taskana.camunda.client.CamundaTaskClient;
import pro.taskana.camunda.converter.CamundaTaskConverter;
import pro.taskana.camunda.mappings.TimestampMapper;
import pro.taskana.camunda.model.CamundaTask;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

/**
 * Scheduler for receiving Camunda tasks and completing Taskana tasks.
 *
 * @author kkl
 */
@Component
public class Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private List<String> camundaHosts;

    @Autowired
    private CamundaTaskClient camundaClient;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CamundaTaskConverter camundaTaskConverter;

    @Autowired
    private TimestampMapper timestampMapper;

    @Autowired
    public Scheduler(@Value("${camundaHosts}") final String camundaHostNames) {
        initCamundaHosts(camundaHostNames);
    }

    @Scheduled(fixedRate = 5000)
    public void createTaskanaTasksOfCamundaTasks()
        throws DomainNotFoundException, InvalidWorkbasketException, NotAuthorizedException,
        WorkbasketAlreadyExistException, ClassificationAlreadyExistException, InvalidArgumentException,
        WorkbasketNotFoundException, ClassificationNotFoundException, TaskAlreadyExistException {

        Instant createdAfter = timestampMapper.getLatestTimestampOfCreated();
        Instant now = Instant.now();
        for (String camundaHost : camundaHosts) {
            for (CamundaTask camundaTask : camundaClient.retrieveCamundaTasks(camundaHost, createdAfter)) {
                Task taskanaTask = camundaTaskConverter.toTaskanaTask(camundaTask, camundaHost);
                taskanaTask = taskService.createTask(taskanaTask);
                LOGGER.info("Task \"" + taskanaTask.getName() + "\" with Taskana ID " + taskanaTask.getId()
                    + "and Camunda ID " + camundaTask.getId() + " created");
            }
            timestampMapper.clearCreateTable();
            timestampMapper.insertCreated(UUID.randomUUID().toString(), now);
        }
    }

    @Scheduled(fixedRate = 5000)
    public void completeTaskanaTasks() throws InterruptedException, TaskNotFoundException, NotAuthorizedException {
        Instant now = Instant.now();
        Instant completedAfter = timestampMapper.getLatestTimestampOfCompleted();
        TimeInterval completedIn = new TimeInterval(completedAfter, now);

        List<TaskSummary> taskanaTaskSummaries = taskService.createTaskQuery()
            .stateIn(TaskState.COMPLETED)
            .completedWithin(completedIn)
            .list();

        for (TaskSummary taskanaTaskSummary : taskanaTaskSummaries) {
            Task task = taskService.getTask(taskanaTaskSummary.getTaskId());
            ResponseEntity<String> res = camundaClient.completeCamundaTask(task.getCallbackInfo().get("camunda_host"),
                task.getCallbackInfo().get("camunda_task_id"));
            if (res.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                LOGGER.info("Task \"" + task.getName() + "\" with Taskana ID " + task.getId()
                    + "and Camunda ID " + task.getCallbackInfo().get("camunda_task_id") + " completed");
            }
        }
        timestampMapper.clearCompletedTable();
        timestampMapper.insertCompleted(UUID.randomUUID().toString(), now);
    }

    private void initCamundaHosts(String camundaHostsNames) {
        List<String> camundaHosts = new ArrayList<String>();
        if (camundaHostsNames != null && !camundaHostsNames.isEmpty()) {
            StringTokenizer st = new StringTokenizer(camundaHostsNames, ",");
            while (st.hasMoreTokens()) {
                camundaHosts.add(st.nextToken().trim().toLowerCase());
            }
        }
        LOGGER.info("Init cmaunda hosts: {}", camundaHosts);
        this.camundaHosts = camundaHosts;
    }

}
