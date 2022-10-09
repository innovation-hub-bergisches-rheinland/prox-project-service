package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.innovationhub.prox.projectservice.project.dto.CreateProjectFromProposal;
import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.event.ProposalReceivedCommitment;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ProposalCommitmentEventListenerTest {
  ProposalRepository proposalRepository = mock(ProposalRepository.class);
  ProjectService projectService = mock(ProjectService.class);

  ProposalCommitmentEventListener listener = new ProposalCommitmentEventListener(proposalRepository, projectService);

  @Test
  void shouldCreateNewProjectFromProposal() {
    var proposalId = UUID.randomUUID();
    var supervisorId = UUID.randomUUID();
    var promoted = new ProposalReceivedCommitment(
      proposalId,
      supervisorId
    );
    var proposal = mock(Proposal.class);
    when(proposalRepository.findById(proposalId)).thenReturn(Optional.of(proposal));

    listener.createProjectOfProposal(promoted);

    var request = ArgumentCaptor.forClass(CreateProjectFromProposal.class);
    verify(projectService).createProjectFromProposal(request.capture());
    assertThat(request.getValue().proposal())
      .isEqualTo(proposal);
  }
}
