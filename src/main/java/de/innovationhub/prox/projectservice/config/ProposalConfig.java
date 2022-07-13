package de.innovationhub.prox.projectservice.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

// TODO: Refactor, Generate Metadata and try to use records
@Getter
@ConfigurationProperties(prefix = "project-service.proposals")
public class ProposalConfig {

  private final Boolean enableAutoArchive;
  @DurationUnit(ChronoUnit.DAYS)
  private final Duration archiveAfter;
  private final Boolean enableAutoDeleteMarking;
  @DurationUnit(ChronoUnit.DAYS)
  private final Duration markForDeletionAfter;
  private final Boolean enableAutoDeletion;

  @ConstructorBinding
  public ProposalConfig(
      @DefaultValue("true") Boolean enableAutoArchive,
      @DurationUnit(ChronoUnit.DAYS) @DefaultValue("90") Duration archiveAfter,
      @DefaultValue("true") Boolean enableAutoDeleteMarking,
      @DurationUnit(ChronoUnit.DAYS) @DefaultValue("30") Duration markForDeletionAfter,
      @DefaultValue("true") Boolean enableAutoDeletion) {
    this.enableAutoArchive = enableAutoArchive;
    this.archiveAfter = archiveAfter;
    this.enableAutoDeleteMarking = enableAutoDeleteMarking;
    this.markForDeletionAfter = markForDeletionAfter;
    this.enableAutoDeletion = enableAutoDeletion;
  }
}
