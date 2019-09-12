package io.archilab.prox.projectservice.config;

import io.archilab.prox.projectservice.module.StudyCourseService;
import io.archilab.prox.projectservice.tags.TagCounterUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
  private StudyCourseService studyCourseService;

  @Autowired
  private TagCounterUpdater tagCounterUpdater;


  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());

    // StudyCourseService
    taskRegistrar.addTriggerTask(() -> studyCourseService.importStudyCourses(), triggerContext -> {

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

    // TagCounterService
    taskRegistrar.addTriggerTask(() -> tagCounterUpdater.updateTagCounter(), triggerContext -> {

      Calendar nextExecutionTime = new GregorianCalendar();

      nextExecutionTime.add(Calendar.SECOND,
          Integer.valueOf(env.getProperty("tagRecommendationCalculation.delay.seconds")));
      return nextExecutionTime.getTime();
    });
  }
}
