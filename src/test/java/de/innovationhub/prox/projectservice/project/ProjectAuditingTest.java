package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;

@SpringBootTest
@AutoConfigureDataJpa
@Transactional
class ProjectAuditingTest {

  @Autowired
  private EntityManager entityManager;

  @MockBean
  private AuditorAware<UUID> auditorAware;

  @Test
  void shouldSaveCreatedBy() {
    // Given
    var creatorId = UUID.randomUUID();
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);
    when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(creatorId));

    // When
    entityManager.persist(randomProject);
    entityManager.flush();

    // Then
    var saved = entityManager.find(Project.class, randomProject.getId());
    assertThat(saved.getCreatorID()).isEqualByComparingTo(creatorId);

    verify(auditorAware).getCurrentAuditor();
  }

  @Test
  void shouldSaveCreationDate() {
    // Given
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);

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
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);
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
}
