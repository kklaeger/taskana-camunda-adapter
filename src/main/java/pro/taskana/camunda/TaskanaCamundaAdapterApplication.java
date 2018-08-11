package pro.taskana.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application that centralizes the tasks of several Camunda task lists in Taskana.
 * 
 * @author kkl
 */
@SpringBootApplication
@EnableScheduling
public class TaskanaCamundaAdapterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskanaCamundaAdapterApplication.class, args);
    }

}
