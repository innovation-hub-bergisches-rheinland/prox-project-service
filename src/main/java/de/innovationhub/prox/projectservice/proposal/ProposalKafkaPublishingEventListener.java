package de.innovationhub.prox.projectservice.proposal;

import de.innovationhub.prox.projectservice.proposal.event.ProposalChanged;
import de.innovationhub.prox.projectservice.proposal.event.ProposalDeleted;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Publishes certain events to Kafka as integration events.
 */
@Component
public class ProposalKafkaPublishingEventListener {
  private static final String PROPOSAL_TOPIC = "entity.proposal.proposal";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public ProposalKafkaPublishingEventListener(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  @EventListener
  public void publishEntityState(ProposalChanged proposalChanged) {
    this.kafkaTemplate.send(PROPOSAL_TOPIC, proposalChanged.proposal().getId().toString(), proposalChanged.proposal());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  @EventListener
  public void publishTombstone(ProposalDeleted proposalDeleted) {
    this.kafkaTemplate.send(PROPOSAL_TOPIC, proposalDeleted.proposalId().toString(), null);
  }
}
