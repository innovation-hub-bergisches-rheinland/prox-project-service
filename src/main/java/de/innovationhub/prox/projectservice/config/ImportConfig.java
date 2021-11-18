package de.innovationhub.prox.projectservice.config;

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
