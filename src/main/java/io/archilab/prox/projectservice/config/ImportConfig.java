package io.archilab.prox.projectservice.config;

import io.archilab.prox.projectservice.module.ModuleRepository;
import io.archilab.prox.projectservice.module.StudyCourseRepository;
import io.archilab.prox.projectservice.module.StudyCourseService;
import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ImportConfig implements SchedulingConfigurer {

  private final Logger logger = LoggerFactory.getLogger(StudyCourseService.class);

  @Autowired
  private Environment env;

  private boolean initialStart = true;

  @Bean
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(100);
  }


  @Autowired
  private ModuleRepository moduleRepository;
  @Autowired
  private StudyCourseRepository studyCourseRepository;
  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private StudyCourseService studyCourseService;

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());

    taskRegistrar.addTriggerTask(() -> {
              // studyCourseService.importStudyCourses();

              this.projectRepository.deleteAll();
              this.moduleRepository.deleteAll();
              this.studyCourseRepository.deleteAll();

            },
            triggerContext -> {

              Calendar nextExecutionTime = new GregorianCalendar();

              if (initialStart) {
                initialStart = false;
                nextExecutionTime.add(Calendar.SECOND,
                        Integer.valueOf(env.getProperty("moduleImport.delay.initial.seconds")));
                return nextExecutionTime.getTime();
              }

              boolean hasData = studyCourseService.hasData();

              if (hasData) {
                logger.info("importData: has data");
                nextExecutionTime.add(Calendar.MINUTE,
                        Integer.valueOf(env.getProperty("moduleImport.delay.hasData.minutes")));
              } else {
                logger.info("importData: has no data");
                nextExecutionTime.add(Calendar.SECOND,
                        Integer.valueOf(env.getProperty("moduleImport.delay.hasNoData.seconds")));
              }

              return nextExecutionTime.getTime();
            });
  }
}
