package de.innovationhub.prox.projectservice.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

/*@Getter
@ConfigurationProperties(prefix = "project-service.proposals")
public class ProposalConfig {
  private final ProposalJobConfig jobs;

  @ConstructorBinding
  public ProposalConfig(@DefaultValue ProposalJobConfig jobs) {
    this.jobs = jobs;
  }

  @Getter
  public class ProposalJobConfig {
    private final AutoArchiveConfig autoArchive;
    private final AutoMarkForDeletionConfig autoMarkForDelete;
    private final AutoDeletionConfig autoDelete;

    public ProposalJobConfig(@DefaultValue AutoArchiveConfig autoArchive,
        @DefaultValue AutoMarkForDeletionConfig autoMarkForDelete, @DefaultValue AutoDeletionConfig autoDelete) {
      this.autoArchive = autoArchive;
      this.autoMarkForDelete = autoMarkForDelete;
      this.autoDelete = autoDelete;
    }

    @Getter
    public class AutoArchiveConfig {
      private final Boolean enable;
      private final Duration after;
      private final String cron;

      public AutoArchiveConfig(
          @DefaultValue("true") Boolean enable,
          @DurationUnit(ChronoUnit.DAYS) @DefaultValue("90") Duration after,
          @DefaultValue("0 0 0 * * *") String cron) {
        this.enable = enable;
        this.after = after;
        this.cron = cron;
      }
    }

    @Getter
    public class AutoMarkForDeletionConfig {
      private final Boolean enable;
      private final Duration after;
      private final String cron;
      public AutoMarkForDeletionConfig(
          @DefaultValue("true") Boolean enable,
          @DurationUnit(ChronoUnit.DAYS) @DefaultValue("30") Duration after,
          @DefaultValue("0 0 0 * * *") String cron) {
        this.enable = enable;
        this.after = after;
        this.cron = cron;
      }
    }

    @Getter
    public class AutoDeletionConfig {
      private final Boolean enable;
      private final String cron;
      public AutoDeletionConfig(
          @DefaultValue("true") Boolean enable,
          @DefaultValue("0 0 0 * * *") String cron) {
        this.enable = enable;
        this.cron = cron;
      }
    }
  }
}*/

@ConfigurationProperties(prefix = "project-service.proposals")
public record ProposalConfig(@DefaultValue ProposalJobConfig jobs) {

  public record ProposalJobConfig(@DefaultValue AutoArchiveConfig autoArchive,
                                  @DefaultValue AutoMarkForDeletionConfig autoMarkForDelete,
                                  @DefaultValue AutoDeletionConfig autoDelete) {

    public record AutoArchiveConfig(@DefaultValue("true") Boolean enable,
                                    @DurationUnit(ChronoUnit.DAYS) @DefaultValue("90") Duration after
                                    // @DefaultValue("0 0 0 * * *") String cron
    ) {

    }

    public record AutoMarkForDeletionConfig(@DefaultValue("true") Boolean enable,
                                            @DurationUnit(ChronoUnit.DAYS) @DefaultValue("90") Duration after
                                            // @DefaultValue("0 0 0 * * *") String cron
    ) {

    }

    public record AutoDeletionConfig(@DefaultValue("true") Boolean enable
                                     // @DefaultValue("0 0 0 * * *") String cron
    ) {

    }
  }
}
