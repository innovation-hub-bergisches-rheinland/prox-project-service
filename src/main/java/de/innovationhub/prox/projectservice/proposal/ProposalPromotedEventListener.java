package de.innovationhub.prox.projectservice.proposal;

import de.innovationhub.prox.projectservice.project.event.ProposalPromotedToProject;
import javax.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProposalPromotedEventListener {
  private final ProposalRepository proposalRepository;

  public ProposalPromotedEventListener(ProposalRepository proposalRepository) {
    this.proposalRepository = proposalRepository;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @EventListener
  @Transactional
  public void createProjectOfProposal(ProposalPromotedToProject event) {
    var proposal = this.proposalRepository.findById(event.proposalId())
      .orElseThrow();

    proposal.setProjectId(event.projectId());
    this.proposalRepository.save(proposal);
  }
}
