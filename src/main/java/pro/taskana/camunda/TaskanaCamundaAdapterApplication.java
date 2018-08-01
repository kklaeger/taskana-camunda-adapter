package pro.taskana.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskanaCamundaAdapterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskanaCamundaAdapterApplication.class, args);
    }

}
