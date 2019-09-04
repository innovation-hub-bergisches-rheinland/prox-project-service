package io.archilab.prox.projectservice.config;

import io.archilab.prox.projectservice.module.StudyCourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class ImportConfig {
	
	@Autowired
	StudyCourseService studyCourseService;

//  @Bean
//  CommandLineRunner runFeign(StudyCourseService studyCourseService) {
//    return args -> studyCourseService.importStudyCourses();
//  }
  
  @Scheduled(fixedDelay = (1000)*60*60)
  protected void scheduledImport()
  {

	  studyCourseService.importStudyCourses();
  }

}
