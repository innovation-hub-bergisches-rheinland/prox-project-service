package io.archilab.prox.projectservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class ProjectService {

  public static void main(String[] args) {
    SpringApplication.run(ProjectService.class, args);
  }
}


