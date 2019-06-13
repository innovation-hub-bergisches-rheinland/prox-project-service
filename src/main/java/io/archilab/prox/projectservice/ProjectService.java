package io.archilab.prox.projectservice;

import io.archilab.prox.projectservice.module.StudyCourseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@EnableFeignClients
public class ProjectService {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(ProjectService.class, args);
    context.getBean(StudyCourseService.class).importStudyCourses();
  }
}




