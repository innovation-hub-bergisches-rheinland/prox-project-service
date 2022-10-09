package de.innovationhub.prox.projectservice.proposal;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import de.innovationhub.prox.projectservice.project.event.ProposalPromotedToProject;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProposalPromotedEventListenerTest {
  ProposalRepository proposalRepository = mock(ProposalRepository.class);
  ProposalPromotedEventListener listener = new ProposalPromotedEventListener(proposalRepository);


  @Test
  void shouldAddProjectReferenceToProposal() {
    var proposalId = UUID.randomUUID();
    var projectId = UUID.randomUUID();
    var promoted = new ProposalPromotedToProject(
      proposalId,
      projectId
    );
    var proposal = mock(Proposal.class);
    when(proposalRepository.findById(proposalId)).thenReturn(Optional.of(proposal));

    listener.addProjectReferenceToProposal(promoted);

    verify(proposal).setProjectId(projectId);
    verify(proposalRepository).save(proposal);
  }
}
