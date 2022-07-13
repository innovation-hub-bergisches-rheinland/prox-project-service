package de.innovationhub.prox.projectservice.proposal.jobs;

import de.innovationhub.prox.projectservice.config.ProposalConfig;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Instant;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "project-service.proposals", value = "enableAutoArchive", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ProposalAutoArchiver {
  private final ProposalRepository proposalRepository;
  private final ProposalConfig proposalConfig;

  public ProposalAutoArchiver(ProposalRepository proposalRepository, ProposalConfig proposalConfig) {
    this.proposalRepository = proposalRepository;
    this.proposalConfig = proposalConfig;
  }

  @PostConstruct
  void log() {
    log.info("Proposal Auto Archiving is enabled with a duration of {} for archiving", proposalConfig.getArchiveAfter());
  }

  // TODO: is a cron really the best idea?
  @Scheduled(cron = "0 0 0 * * *")
  void autoArchive() {
    var qualifyingTimestamp = Instant.now().minus(proposalConfig.getArchiveAfter());
    var proposalsToArchive = this.proposalRepository.findWithStatusModifiedBefore(ProposalStatus.PROPOSED, qualifyingTimestamp);
    if(proposalsToArchive.size() > 0) {
      proposalsToArchive.forEach(p -> p.setStatus(ProposalStatus.ARCHIVED));
      this.proposalRepository.saveAll(proposalsToArchive);
    }
  }
}
