package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.project.dto.CreateProjectFromProposal;
import de.innovationhub.prox.projectservice.project.event.ProposalPromotedToProject;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.event.ProposalReceivedCommitment;
import javax.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProposalCommitmentEventListener {
  private final ProposalRepository proposalRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ProjectService projectService;

  public ProposalCommitmentEventListener(ProposalRepository proposalRepository,
    ApplicationEventPublisher applicationEventPublisher,
    ProjectService projectService) {
    this.proposalRepository = proposalRepository;
    this.applicationEventPublisher = applicationEventPublisher;
    this.projectService = projectService;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @EventListener
  @Transactional
  public void createProjectOfProposal(ProposalReceivedCommitment promoted) {
    var proposal = this.proposalRepository.findById(promoted.proposalId())
      .orElseThrow();

    var request = new CreateProjectFromProposal(proposal);
    var project = projectService.createProjectFromProposal(request);
    var event = new ProposalPromotedToProject(proposal.getId(), project.id());
    this.applicationEventPublisher.publishEvent(event);
  }
}
