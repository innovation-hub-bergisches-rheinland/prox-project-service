package de.innovationhub.prox.projectservice.proposal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

class ProposalTagListenerTest {
  ProposalRepository proposalRepository = mock(ProposalRepository.class);
  ObjectMapper objectMapper = new ObjectMapper();

  ProposalTagListener proposalTagListener = new ProposalTagListener(proposalRepository, objectMapper);

  @Test
  void shouldSkipWhenNoProposalIsFound() throws JsonProcessingException {
    when(proposalRepository.findById(any())).thenReturn(Optional.empty());
    var event = new ItemTaggedDto(UUID.randomUUID(), Set.of());
    var record = new ConsumerRecord<String, String>("event.id.tagged", 0, 0, null, objectMapper.writeValueAsString(event));

    proposalTagListener.tagProposal(record);

    // No exception
  }

  @Test
  void shouldAddTags() throws JsonProcessingException {
    var proposal = new Proposal();
    when(proposalRepository.findById(eq(proposal.getId()))).thenReturn(Optional.of(proposal));
    var event = new ItemTaggedDto(proposal.getId(), Set.of("tag1", "tag2"));
    var record = new ConsumerRecord<String, String>("event.id.tagged", 0, 0, null, objectMapper.writeValueAsString(event));

    proposalTagListener.tagProposal(record);

    assert proposal.getTags().contains("tag1");
    assert proposal.getTags().contains("tag2");
  }
}
