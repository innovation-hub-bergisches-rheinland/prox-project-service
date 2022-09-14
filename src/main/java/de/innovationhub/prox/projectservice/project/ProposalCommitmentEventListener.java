package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.project.dto.CreateProjectFromProposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.event.ProposalReceivedCommitment;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProposalCommitmentEventListener {
  private final ProposalRepository proposalRepository;
  private final ProjectService projectService;

  public ProposalCommitmentEventListener(ProposalRepository proposalRepository,
    ProjectService projectService) {
    this.proposalRepository = proposalRepository;
    this.projectService = projectService;
  }

  @EventListener
  public void createProjectOfProposal(ProposalReceivedCommitment promoted) {
    var proposal = this.proposalRepository.findById(promoted.proposalId())
      .orElseThrow();

    var request = new CreateProjectFromProposal(proposal);
    var project = projectService.createProjectFromProposal(request);
  }
}
