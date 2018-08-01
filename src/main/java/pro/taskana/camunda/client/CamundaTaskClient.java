package pro.taskana.camunda.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pro.taskana.camunda.model.CamundaTask;

@Component
public class CamundaTaskClient {

    private static final String URL_GET_CAMUNDA_TASKS = "/task/";
    private static final String COMPLETE_TASK = "/complete/";
    private static final String EMPTY_REQUEST_BODY = "{}";

    private final RestTemplate restTemplate;
    private final String camundaHost;

    @Autowired
    public CamundaTaskClient(final RestTemplate restTemplate, @Value("${camundaHost}") final String camundaHost) {
        this.restTemplate = restTemplate;
        this.camundaHost = camundaHost;
    }

    public CamundaTask[] retrieveCamundaTasks() {
        return restTemplate.getForObject(camundaHost + "/task/", CamundaTask[].class);
    }

    public ResponseEntity<String> completeCamundaTask(CamundaTask task) {
        String url = camundaHost + URL_GET_CAMUNDA_TASKS + task.getId() + COMPLETE_TASK;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(EMPTY_REQUEST_BODY, headers);
        return restTemplate.postForEntity(url, entity, String.class);
    }

}
