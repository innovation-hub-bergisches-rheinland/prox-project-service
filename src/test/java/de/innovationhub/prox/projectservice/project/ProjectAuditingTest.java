package de.innovationhub.prox.projectservice.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    // Then
    var saved = entityManager.find(Project.class, randomProject.getId());
    assertThat(saved.getCreatorID()).isEqualByComparingTo(creatorId);
  }
}