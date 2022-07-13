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

  /**
   * Enable the auto archiving of proposals.
   */
  private final Boolean enableAutoArchive;
  /**
   * Time after last status change to archive a proposal in days.
   */
  @DurationUnit(ChronoUnit.DAYS)
  private final Duration autoArchiveAfter;
  /**
   * Enable auto marking for deletion of proposals.
   */
  private final Boolean enableAutoMarkForDeletion;
  /**
   * Time after last status change to mark a proposal for deletion in days.
   */
  @DurationUnit(ChronoUnit.DAYS)
  private final Duration autoMarkDeletionAfter;
  /**
   * Enable auto deletion of proposals.
   */
  private final Boolean enableAutoDeletion;

  @ConstructorBinding
  public ProposalConfig(
      @DefaultValue("true") Boolean enableAutoArchive,
      @DurationUnit(ChronoUnit.DAYS) @DefaultValue("90") Duration autoArchiveAfter,
      @DefaultValue("true") Boolean enableAutoMarkForDeletion,
      @DurationUnit(ChronoUnit.DAYS) @DefaultValue("30") Duration autoMarkDeletionAfter,
      @DefaultValue("true") Boolean enableAutoDeletion) {
    this.enableAutoArchive = enableAutoArchive;
    this.autoArchiveAfter = autoArchiveAfter;
    this.enableAutoMarkForDeletion = enableAutoMarkForDeletion;
    this.autoMarkDeletionAfter = autoMarkDeletionAfter;
    this.enableAutoDeletion = enableAutoDeletion;
  }
}
