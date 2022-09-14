package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.project.event.ProjectChanged;
import de.innovationhub.prox.projectservice.project.event.ProjectDeleted;
import de.innovationhub.prox.projectservice.project.event.ProposalPromotedToProject;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Publishes certain events to Kafka as integration events.
 */
@Component
public class ProjectKafkaPublishingEventListener {
  private static final String PROJECT_TOPIC = "entity.project.project";
  private static final String PROPOSAL_PROMOTED_TOPIC = "event.proposal.promoted-to-project";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public ProjectKafkaPublishingEventListener(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  @EventListener
  public void publishEntityState(ProjectChanged projectChanged) {
    this.kafkaTemplate.send(PROJECT_TOPIC, projectChanged.project().getId().toString(), projectChanged.project());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  @EventListener
  public void publishTombstone(ProjectDeleted projectDeleted) {
    this.kafkaTemplate.send(PROJECT_TOPIC, projectDeleted.projectId().toString(), null);
  }

  // TODO: This event is not ideal, as it indicates a change in the proposal (also the topic
  //  description represents that) although it should indicate that a new project has been
  //  generated. This would require us to deprecate the kafka topic, which would break (at least)
  //  the tag service. We should do this soon.
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  @EventListener
  public void publishPromotion(ProposalPromotedToProject proposalPromotedToProject) {
    this.kafkaTemplate.send(PROPOSAL_PROMOTED_TOPIC, proposalPromotedToProject.proposalId().toString(), proposalPromotedToProject);
  }
}
