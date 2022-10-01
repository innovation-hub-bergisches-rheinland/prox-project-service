package de.innovationhub.prox.projectservice.proposal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProposalTagListenerTest {
  ProposalRepository proposalRepository = mock(ProposalRepository.class);

  ProposalTagListener proposalTagListener = new ProposalTagListener(proposalRepository);

  @Test
  void shouldSkipWhenNoProposalIsFound() {
    when(proposalRepository.findById(any())).thenReturn(Optional.empty());

    proposalTagListener.tagProposal(new ItemTaggedDto(UUID.randomUUID(), Set.of()));

    // No exception
  }

  @Test
  void shouldAddTags() {
    var proposal = new Proposal();
    when(proposalRepository.findById(eq(proposal.getId()))).thenReturn(Optional.of(proposal));

    proposalTagListener.tagProposal(new ItemTaggedDto(proposal.getId(), Set.of(new ItemTaggedDto.Tag("tag1"), new ItemTaggedDto.Tag("tag2"))));

    assert proposal.getTags().contains("tag1");
    assert proposal.getTags().contains("tag2");
  }
}
