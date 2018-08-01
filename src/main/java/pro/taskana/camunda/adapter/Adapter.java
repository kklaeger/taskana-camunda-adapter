package pro.taskana.camunda.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pro.taskana.camunda.client.CamundaTaskClient;
import pro.taskana.camunda.model.CamundaTask;

@Component
public class Adapter {

    @Autowired
    CamundaTaskClient camundaClient;

    @Scheduled(fixedRate = 3000)
    public void createTaskanaTasksOfCamundaTasks() {
        CamundaTask[] tasks = camundaClient.retrieveCamundaTasks();
        System.out.println("Number of tasks: " + tasks.length);
    }
}
