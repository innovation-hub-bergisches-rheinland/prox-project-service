package io.archilab.prox.projectservice.module;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportRunner {

  @Bean
  CommandLineRunner runFeign(StudyCourseService studyCourseService) {
    return args -> studyCourseService.importStudyCourses();
  }

}
