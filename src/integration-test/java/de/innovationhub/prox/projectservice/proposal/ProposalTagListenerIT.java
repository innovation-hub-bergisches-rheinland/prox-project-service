package de.innovationhub.prox.projectservice.proposal;

import static de.innovationhub.prox.projectservice.AwaitilityAssertions.assertEventually;
import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.AbstractRedpandaIT;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;

@SpringBootTest
@ActiveProfiles("h2")
class ProposalTagListenerIT extends AbstractRedpandaIT {
  private static final String TOPIC = "event.item.tagged";

  @Autowired
  KafkaTemplate<String, ItemTaggedDto> kafkaTemplate;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ProposalRepository proposalRepository;

  @AfterEach
  void tearDown() {
    proposalRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void shouldTagProposal() throws ExecutionException, InterruptedException, TimeoutException {
    var proposal = createAndPersistProposal();

    var tags = Set.of("tag1", "tag2");
    var event = new ItemTaggedDto(proposal.getId(), tags);

    kafkaTemplate.send(TOPIC, proposal.getId().toString(), event)
      .get(5, TimeUnit.SECONDS);

    assertEventually(() -> {
      var foundProposal = proposalRepository.findById(proposal.getId());
      assertThat(foundProposal)
        .isPresent();
      assertThat(foundProposal.get().getTags()).containsExactlyInAnyOrderElementsOf(tags);
    });
  }

  private Proposal createAndPersistProposal() {
    var owner = new User(UUID.randomUUID(), "Homer Simpson");
    userRepository.save(owner);
    var proposal = Proposal.builder()
      .name("Test Proposal")
      .description("Lol")
      .requirement("Yes")
      .owner(owner)
      .build();
    proposalRepository.save(proposal);
    return proposal;
  }
}
