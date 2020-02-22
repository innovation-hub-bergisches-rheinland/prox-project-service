package io.archilab.prox.projectservice.config;

import io.archilab.prox.projectservice.module.StudyCourseService;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@Slf4j
public class ImportConfig implements SchedulingConfigurer {

  @Autowired private Environment env;

  private boolean initialStart = true;
  @Autowired private StudyCourseService studyCourseService;

  @Bean
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(100);
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(this.taskExecutor());

    taskRegistrar.addTriggerTask(
        () -> this.studyCourseService.importStudyCourses(),
        triggerContext -> {
          Calendar nextExecutionTime = new GregorianCalendar();

          if (this.initialStart) {
            this.initialStart = false;
            nextExecutionTime.add(
                Calendar.SECOND,
                Integer.valueOf(this.env.getProperty("moduleImport.delay.initial.seconds")));
            return nextExecutionTime.getTime();
          }

          boolean hasData = this.studyCourseService.hasData();

          if (hasData) {
            ImportConfig.log.info("importData: has data");
            nextExecutionTime.add(
                Calendar.MINUTE,
                Integer.valueOf(this.env.getProperty("moduleImport.delay.hasData.minutes")));
          } else {
            ImportConfig.log.info("importData: has no data");
            nextExecutionTime.add(
                Calendar.SECOND,
                Integer.valueOf(this.env.getProperty("moduleImport.delay.hasNoData.seconds")));
          }

          return nextExecutionTime.getTime();
        });
  }
}
