package de.innovationhub.prox.projectservice.proposal.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import de.innovationhub.prox.projectservice.proposal.jobs.ProposalAutoDeleteTest.DeleteTest.DeleteTestContextInitializer;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;

@SpringBootTest
class ProposalAutoDeleteTest extends BaseProposalJobsTest {
  @Autowired ProposalRepository proposalRepository;

  @Autowired ProposalAutoDelete autoDeletion;

  @ContextConfiguration(initializers = {DeleteTestContextInitializer.class})
  @EnabledIfEnvironmentVariable(
      disabledReason = "Long running tests are disabled",
      named = "CI",
      matches = "true")
  @Nested
  class DeleteTest {

    static class DeleteTestContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      @Override
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            configurableApplicationContext,
            "project-service.proposals.jobs.auto-archive.enable=false",
            "project-service.proposals.jobs.auto-mark-for-delete.enable=false",
            "project-service.proposals.jobs.auto-delete.enable=true",
            "project-service.proposals.jobs.auto-delete.cron=* * * * * *");
      }
    }

    @Test
    void shouldDelete() {
      // We create a proposal which has been proposed two days ago
      var proposal = getSampleProposal(ProposalStatus.READY_FOR_DELETION, Instant.now());
      proposal = proposalRepository.save(proposal);

      final var proposalId = proposal.getId();
      // Cron is configured to run every second
      await()
          .atMost(Duration.ofSeconds(2))
          .untilAsserted(() -> assertThat(proposalRepository.existsById(proposalId)).isFalse());
    }
  }

  @Test
  void shouldDelete() {
    // Given
    var proposal = getSampleProposal(ProposalStatus.READY_FOR_DELETION, Instant.now());
    proposalRepository.save(proposal);

    // When
    this.autoDeletion.autoDelete();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal).isEmpty();
  }

  @ParameterizedTest(name = "should not delete {0}")
  @EnumSource(
      value = ProposalStatus.class,
      names = {"ARCHIVED", "PROPOSED"})
  void shouldNotDeleteProposalInOtherState(ProposalStatus proposalStatus) {
    // Given
    var olderTimestamp = Instant.now().minus(Duration.ofDays(2));
    var proposal = getSampleProposal(proposalStatus, olderTimestamp);
    proposalRepository.save(proposal);

    // When
    this.autoDeletion.autoDelete();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal).isPresent().get().isEqualTo(proposal);
  }
}
