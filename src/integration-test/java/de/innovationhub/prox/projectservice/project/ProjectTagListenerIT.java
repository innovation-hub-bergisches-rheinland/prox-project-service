package de.innovationhub.prox.projectservice.project;

import static de.innovationhub.prox.projectservice.AwaitilityAssertions.assertEventually;
import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.AbstractRedpandaIT;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class ProjectTagListenerIT extends AbstractRedpandaIT {
  private static final String TOPIC = "event.item.tagged";

  @Autowired
  KafkaTemplate<String, ItemTaggedDto> kafkaTemplate;

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  UserRepository userRepository;

  @AfterEach
  void tearDown() {
    projectRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void shouldTagProposal() throws ExecutionException, InterruptedException, TimeoutException {
    var project = createAndPersistProject();
    var tags = Set.of("tag1", "tag2");
    var event = new ItemTaggedDto(project.getId(), tags);

    kafkaTemplate.send(TOPIC, project.getId().toString(), event)
      .get(5, TimeUnit.SECONDS);

    assertEventually(() -> {
      var foundProject = projectRepository.findById(project.getId());
      assertThat(foundProject)
        .isPresent();
      assertThat(foundProject.get().getTags()).containsExactlyInAnyOrderElementsOf(tags);
    });
  }

  private Project createAndPersistProject() {
    var owner = new User(UUID.randomUUID(), "Homer Simpson");
    userRepository.save(owner);
    var project = Project.builder()
      .name("Test Project")
      .description("Lol")
      .requirement("Yes")
      .shortDescription("xD")
      .status(ProjectStatus.RUNNING)
      .owner(owner)
      .build();
    projectRepository.save(project);
    return project;
  }
}
