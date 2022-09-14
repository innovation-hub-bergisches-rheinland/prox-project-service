package de.innovationhub.prox.projectservice.proposal;

import de.innovationhub.prox.projectservice.project.event.ProposalPromotedToProject;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProposalPromotedEventListener {
  private final ProposalRepository proposalRepository;

  public ProposalPromotedEventListener(ProposalRepository proposalRepository) {
    this.proposalRepository = proposalRepository;
  }

  @EventListener
  public void addProjectReferenceToProposal(ProposalPromotedToProject event) {
    var proposal = this.proposalRepository.findById(event.proposalId())
      .orElseThrow();

    proposal.setProjectId(event.projectId());
    this.proposalRepository.save(proposal);
  }
}
