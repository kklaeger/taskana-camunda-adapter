package pro.taskana.camunda.scheduler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.camunda.client.CamundaTaskClient;
import pro.taskana.camunda.converter.CamundaTaskConverter;
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

@Component
public class Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private CamundaTaskClient camundaClient;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CamundaTaskConverter camundaTaskConverter;

    private Map<String, String> taskanaTaskIdToCamundaTaskId;

    @Scheduled(fixedRate = 1000)
    public void createTaskanaTasksOfCamundaTasks() throws WorkbasketNotFoundException, ClassificationNotFoundException,
        NotAuthorizedException, TaskAlreadyExistException, InvalidArgumentException, DomainNotFoundException,
        InvalidWorkbasketException, WorkbasketAlreadyExistException, ClassificationAlreadyExistException {

        for (CamundaTask camundaTask : camundaClient.retrieveCamundaTasks()) {
            if (!this.taskanaTaskIdToCamundaTaskId.containsValue(camundaTask.getId())) {
                Task taskanaTask = camundaTaskConverter.toTaskanaTask(camundaTask);
                taskanaTask = taskService.createTask(taskanaTask);
                this.taskanaTaskIdToCamundaTaskId.put(taskanaTask.getId(), camundaTask.getId());
                LOGGER.info("Task \"" + taskanaTask.getName() + "\" with Taskana ID " + taskanaTask.getId()
                    + "and Camunda ID " + camundaTask.getId() + " created");
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void completeTaskanaTasks() throws InterruptedException, TaskNotFoundException, NotAuthorizedException {

        List<TaskSummary> taskanaTasks = taskService.createTaskQuery().stateIn(TaskState.COMPLETED).list();
        for (TaskSummary taskanaTask : taskanaTasks) {
            if (this.taskanaTaskIdToCamundaTaskId.containsKey(taskanaTask.getTaskId())) {
                String camundaTaskId = this.taskanaTaskIdToCamundaTaskId.get(taskanaTask.getTaskId());
                ResponseEntity<String> res = camundaClient.completeCamundaTask(camundaTaskId);
                if (res.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                    this.taskanaTaskIdToCamundaTaskId.remove(taskanaTask.getTaskId());
                    LOGGER.info("Task \"" + taskanaTask.getName() + "\" with Taskana ID " + taskanaTask.getTaskId()
                        + "and Camunda ID " + camundaTaskId + " completed");
                }
            }
        }
    }

    @PostConstruct
    public void setUp() throws TaskNotFoundException, NotAuthorizedException {
        this.taskanaTaskIdToCamundaTaskId = new HashMap<>();
        CamundaTask[] camundaTasks = camundaClient.retrieveCamundaTasks();
        List<TaskSummary> taskanaTasks = taskService.createTaskQuery().list();
        List<String> camundaTaskIds = Arrays.stream(camundaTasks).map(CamundaTask::getId).collect(
            Collectors.toList());
        for (TaskSummary taskanaTask : taskanaTasks) {
            Task task = taskService.getTask(taskanaTask.getTaskId());
            String camundaTaskId = task.getCallbackInfo().get("camunda_task_id");

            if (camundaTaskIds.contains(camundaTaskId)) {
                this.taskanaTaskIdToCamundaTaskId.put(task.getId(), camundaTaskId);
            }

        }
    }

}
