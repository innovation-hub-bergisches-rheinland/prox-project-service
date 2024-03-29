package de.innovationhub.prox.projectservice.proposal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import de.innovationhub.prox.projectservice.owners.user.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("h2")
class ProposalAuditingIT {
  @Autowired private EntityManager entityManager;

  @Test
  void shouldSaveCreationDate() throws InterruptedException {
    // Given
    var proposal = getTestProposal();
    var defaultInstant = Instant.from(proposal.getCreatedAt());

    // Ensure that the creation date will be in the future
    Thread.sleep(1);

    // When
    entityManager.persist(proposal);
    entityManager.flush();

    // Then
    var saved = entityManager.find(Proposal.class, proposal.getId());
    assertThat(saved.getCreatedAt())
        .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS))
        .isAfter(defaultInstant);
  }

  @Test
  void shouldSaveModificationDate() throws InterruptedException {
    // Given
    var proposal = getTestProposal();
    entityManager.persist(proposal);
    entityManager.flush();
    var saved = entityManager.find(Proposal.class, proposal.getId());
    var oldModifiedDate = saved.getModifiedAt();

    // When

    // Ensure that the modification date will be in the future
    Thread.sleep(1);

    saved.setName("Test 123");
    entityManager.persist(proposal);
    entityManager.flush();

    // Then
    var newSaved = entityManager.find(Proposal.class, proposal.getId());
    assertThat(newSaved.getModifiedAt())
        .isAfter(oldModifiedDate)
        .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
  }

  private Proposal getTestProposal() {
    return Proposal.builder().name("Test").description("Test").ownerId(UUID.randomUUID()).build();
  }
}
