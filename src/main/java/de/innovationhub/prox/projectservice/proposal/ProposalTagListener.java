package de.innovationhub.prox.projectservice.proposal;

import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProposalTagListener {
  private final ProposalRepository proposalRepository;

  public ProposalTagListener(ProposalRepository proposalRepository) {
    this.proposalRepository = proposalRepository;
  }

  @KafkaListener(topics = "event.item.tagged", groupId = "proposal-tag-listener")
  public void tagProposal(ItemTaggedDto event) {
    var optionalProposal = proposalRepository.findById(event.item());
    if(optionalProposal.isEmpty()) {
      // OK, we don't care
      return;
    }
    var proposal = optionalProposal.get();
    proposal.setTags(event.tags().stream().map(ItemTaggedDto.Tag::tag).toList());
    proposalRepository.save(proposal);
  }
}
