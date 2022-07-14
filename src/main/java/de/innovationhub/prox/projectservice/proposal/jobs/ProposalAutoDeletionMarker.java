package de.innovationhub.prox.projectservice.proposal.jobs;

import de.innovationhub.prox.projectservice.config.ProposalConfig;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "project-service.proposals.jobs.auto-mark-for-delete", value = "enable", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ProposalAutoDeletionMarker {

  private final ProposalRepository proposalRepository;
  private final Duration markForDeletionAfter;

  public ProposalAutoDeletionMarker(ProposalRepository proposalRepository,
      ProposalConfig proposalConfig) {
    this.proposalRepository = proposalRepository;
    this.markForDeletionAfter = proposalConfig.jobs().autoMarkForDelete().after();
  }

  @PostConstruct
  void log() {
    log.info("Proposal Auto Deletion Marking is enabled with a duration of {} for marking",
        markForDeletionAfter);
  }

  @Scheduled(cron = "${project-service.proposals.jobs.auto-mark-for-delete.cron:0 0 0 * * *}")
  void autoMark() {
    var qualifyingTimestamp = Instant.now().minus(markForDeletionAfter);
    var proposalsToMark = this.proposalRepository.findWithStatusModifiedBefore(
        ProposalStatus.ARCHIVED, qualifyingTimestamp);
    if (proposalsToMark.size() > 0) {
      proposalsToMark.forEach(p -> p.setStatus(ProposalStatus.READY_FOR_DELETION));
      this.proposalRepository.saveAll(proposalsToMark);
    }
  }
}
