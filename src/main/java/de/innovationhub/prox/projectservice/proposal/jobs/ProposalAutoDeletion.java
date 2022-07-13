package de.innovationhub.prox.projectservice.proposal.jobs;

import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Instant;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "project-service.proposals", value = "enableAutoDeletion", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ProposalAutoDeletion {
  private final ProposalRepository proposalRepository;

  public ProposalAutoDeletion(ProposalRepository proposalRepository) {
    this.proposalRepository = proposalRepository;
  }

  @PostConstruct
  void log() {
    log.info("Proposal Auto Deletion is enabled");
  }

  // TODO: is a cron really the best idea?
  @Scheduled(cron = "0 0 0 * * *")
  void autoArchive() {
    // Just use the current timestamp to ensure forward-compatibility as we might introduce pre-dated
    // drafts
    var qualifyingTimestamp = Instant.now();
    var proposalsToDelete = this.proposalRepository.findWithStatusModifiedBefore(ProposalStatus.READY_FOR_DELETION, qualifyingTimestamp);
    if(proposalsToDelete.size() > 0) {
      this.proposalRepository.deleteAll(proposalsToDelete);
    }
  }
}
