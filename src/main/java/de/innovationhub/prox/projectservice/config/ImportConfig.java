/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.projectservice.config;

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

/*@Configuration
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
}*/
