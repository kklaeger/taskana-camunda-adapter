package pro.taskana.camunda.client;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pro.taskana.camunda.model.CamundaTask;

/**
 * REST client that receives and completes Camunda tasks
 *
 * @author kkl
 */
@Component
public class CamundaTaskClient {

    private static final String URL_GET_CAMUNDA_TASKS = "/task/";
    private static final String COMPLETE_TASK = "/complete/";
    private static final String EMPTY_REQUEST_BODY = "{}";

    private final RestTemplate restTemplate;

    @Autowired
    public CamundaTaskClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CamundaTask[] retrieveCamundaTasks(String camundaHost, Instant createdAfter) {
        String url = camundaHost + "/task/";
        String requestBody;
        if (createdAfter == null) {
            requestBody = EMPTY_REQUEST_BODY;
        } else {
            requestBody = "{\"createdAfter\": \"" + createdAfter.toString().replaceAll("Z$", "+0000") + "\"}";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<CamundaTask[]> tasks = restTemplate.postForEntity(url, entity, CamundaTask[].class);
        return tasks.getBody();
    }

    public ResponseEntity<String> completeCamundaTask(String camundaHost, String camundaTaskId) {
        String url = camundaHost + URL_GET_CAMUNDA_TASKS + camundaTaskId + COMPLETE_TASK;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(EMPTY_REQUEST_BODY, headers);
        return restTemplate.postForEntity(url, entity, String.class);
    }

}
