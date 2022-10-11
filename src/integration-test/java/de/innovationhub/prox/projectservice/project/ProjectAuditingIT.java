package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import de.innovationhub.prox.projectservice.owners.user.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("h2")
class ProjectAuditingIT {

  @Autowired private EntityManager entityManager;

  @Test
  void shouldSaveCreationDate() {
    // Given
    var randomProject = getTestProject();

    // When
    entityManager.persist(randomProject);
    entityManager.flush();

    // Then
    var saved = entityManager.find(Project.class, randomProject.getId());
    assertThat(saved.getCreatedAt()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
  }

  @Test
  void shouldSaveModificationDate() {
    // Given
    var randomProject = getTestProject();
    entityManager.persist(randomProject);
    entityManager.flush();
    var saved = entityManager.find(Project.class, randomProject.getId());
    var oldModifiedDate = saved.getModifiedAt();

    // When
    saved.setName("Test 123");
    entityManager.persist(randomProject);
    entityManager.flush();

    // Then
    var newSaved = entityManager.find(Project.class, randomProject.getId());
    assertThat(newSaved.getModifiedAt())
        .isAfter(oldModifiedDate)
        .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
  }

  private Project getTestProject() {
    return new Project(
        "Test Project",
      "Test Project Description",
      "Test Project Short Description",
      "Test Project Requirement",
      ProjectStatus.AVAILABLE,
      "Test Project Creator Name",
      List.of(new Supervisor(UUID.randomUUID(), "Test Project Supervisor")),
      Collections.emptySet(),
      Collections.emptySet(),
      UUID.randomUUID(),
      null,
      Collections.emptyList(),
      Instant.now(),
      Instant.now());
  }
}
