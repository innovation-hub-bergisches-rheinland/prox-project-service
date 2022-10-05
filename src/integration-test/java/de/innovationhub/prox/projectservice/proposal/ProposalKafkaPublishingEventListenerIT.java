package de.innovationhub.prox.projectservice.proposal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

import de.innovationhub.prox.projectservice.proposal.event.ProposalChanged;
import de.innovationhub.prox.projectservice.proposal.event.ProposalDeleted;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
class ProposalKafkaPublishingEventListenerIT {

  static final String PROPOSAL_TOPIC = "entity.proposal.proposal";

  @MockBean
  KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  ApplicationEventPublisher eventPublisher;

  @Test
  void shouldPublishTombstone() {
    var proposalId = UUID.randomUUID();

    eventPublisher.publishEvent(new ProposalDeleted(proposalId));

    verify(kafkaTemplate).send(eq(PROPOSAL_TOPIC), eq(proposalId.toString()), isNull());
  }

  @Test
  void shouldPublishState() {
    var proposal = new Proposal();

    eventPublisher.publishEvent(new ProposalChanged(proposal));

    verify(kafkaTemplate).send(eq(PROPOSAL_TOPIC), eq(proposal.getId().toString()), eq(proposal));
  }
}
