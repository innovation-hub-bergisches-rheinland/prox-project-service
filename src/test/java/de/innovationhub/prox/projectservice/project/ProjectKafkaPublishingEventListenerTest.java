package de.innovationhub.prox.projectservice.project;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

import de.innovationhub.prox.projectservice.project.event.ProjectChanged;
import de.innovationhub.prox.projectservice.project.event.ProjectDeleted;
import de.innovationhub.prox.projectservice.project.event.ProposalPromotedToProject;
import de.innovationhub.prox.projectservice.proposal.ProposalPromotedEventListener;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
class ProjectKafkaPublishingEventListenerTest {

  static final String PROJECT_TOPIC = "entity.project.project";
  static final String PROPOSAL_PROMOTED_TOPIC = "event.proposal.promoted-to-project";

  @MockBean
  KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  ApplicationEventPublisher eventPublisher;

  @MockBean
  ProposalPromotedEventListener proposalPromotedEventListener;

  @Test
  void shouldPublishTombstone() {
    var projectId = UUID.randomUUID();

    eventPublisher.publishEvent(new ProjectDeleted(projectId));

    verify(kafkaTemplate).send(eq(PROJECT_TOPIC), eq(projectId.toString()), isNull());
  }

  @Test
  void shouldPublishState() {
    var project = new Project();

    eventPublisher.publishEvent(new ProjectChanged(project));

    verify(kafkaTemplate).send(eq(PROJECT_TOPIC), eq(project.getId().toString()), eq(project));
  }

  @Test
  void shouldPublishPromotion() {
    var proposalId = UUID.randomUUID();
    var event = new ProposalPromotedToProject(proposalId, UUID.randomUUID());
    eventPublisher.publishEvent(event);

    verify(kafkaTemplate).send(eq(PROPOSAL_PROMOTED_TOPIC), eq(proposalId.toString()), eq(event));
  }
}
