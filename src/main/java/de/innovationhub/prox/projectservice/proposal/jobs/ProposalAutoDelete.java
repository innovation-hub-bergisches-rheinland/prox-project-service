package de.innovationhub.prox.projectservice.proposal.jobs;

import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Instant;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "project-service.proposals.jobs.auto-delete", value = "enable", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ProposalAutoDelete {
  private final ProposalRepository proposalRepository;

  public ProposalAutoDelete(ProposalRepository proposalRepository) {
    this.proposalRepository = proposalRepository;
  }

  @PostConstruct
  void log() {
    log.info("Proposal Auto Deletion is enabled");
  }

  @Scheduled(cron = "${project-service.proposals.jobs.auto-delete.cron:0 0 0 * * *}")
  void autoDelete() {
    // Just use the current timestamp to ensure forward-compatibility as we might introduce pre-dated
    // drafts
    var qualifyingTimestamp = Instant.now();
    var proposalsToDelete = this.proposalRepository.findWithStatusModifiedBefore(ProposalStatus.READY_FOR_DELETION, qualifyingTimestamp);
    if(!proposalsToDelete.isEmpty()) {
      this.proposalRepository.deleteAll(proposalsToDelete);
    }
  }
}
