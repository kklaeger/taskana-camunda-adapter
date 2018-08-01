package pro.taskana.camunda;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import pro.taskana.camunda.client.CamundaTaskClient;
import pro.taskana.camunda.model.CamundaTask;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExampleTest {

    @Autowired
    CamundaTaskClient camundaClient;

    @Test
    public void test() {
        CamundaTask[] tasks = camundaClient.retrieveCamundaTasks();
        ResponseEntity<String> res = camundaClient.completeCamundaTask(tasks[0]);
    }
}
