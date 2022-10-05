package de.innovationhub.prox.projectservice.proposal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProposalTagListener {
  private final ProposalRepository proposalRepository;
  private final ObjectMapper objectMapper;

  public ProposalTagListener(ProposalRepository proposalRepository, ObjectMapper objectMapper) {
    this.proposalRepository = proposalRepository;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "event.item.tagged", groupId = "proposal-tag-listener")
  public void tagProposal(ConsumerRecord<String, String> event) {
    ItemTaggedDto parsedEvent;
    try {
      parsedEvent = this.objectMapper.readValue(event.value(), ItemTaggedDto.class);
    } catch (JsonProcessingException e) {
      log.error("Could not parse event", e);
      throw new RuntimeException(e);
    }
    var optionalProposal = proposalRepository.findById(parsedEvent.item());
    if(optionalProposal.isEmpty()) {
      // OK, we don't care
      return;
    }
    var proposal = optionalProposal.get();
    proposal.setTags(parsedEvent.tags().stream().toList());
    proposalRepository.save(proposal);
  }
}
