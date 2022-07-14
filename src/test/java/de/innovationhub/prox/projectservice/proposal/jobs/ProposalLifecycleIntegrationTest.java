package de.innovationhub.prox.projectservice.proposal.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "project-service.proposals.jobs.auto-archive.enable=true",
    "project-service.proposals.jobs.auto-archive.after=P2D",
    "project-service.proposals.jobs.auto-archive.cron=* * * * * *",
    "project-service.proposals.jobs.auto-mark-for-delete.enable=true",
    "project-service.proposals.jobs.auto-mark-for-delete.after=P2D",
    "project-service.proposals.jobs.auto-mark-for-delete.cron=* * * * * *",
    "project-service.proposals.jobs.auto-delete.enable=true",
    "project-service.proposals.jobs.auto-delete.cron=* * * * * *",
})
@EnabledIfEnvironmentVariable(disabledReason = "Long running tests are disabled", named = "CI", matches = "true")
class ProposalLifecycleIntegrationTest extends BaseProposalJobsTest {
  @Autowired
  ProposalRepository proposalRepository;

  @AfterEach
  void cleanUp() {
    proposalRepository.deleteAll();
  }

  @SpringBootTest(properties = {
      "project-service.proposals.jobs.auto-archive.enable=true",
      "project-service.proposals.jobs.auto-archive.after=P2D",
      "project-service.proposals.jobs.auto-archive.cron=* * * * * *",
      "project-service.proposals.jobs.auto-mark-for-delete.enable=false",
      "project-service.proposals.jobs.auto-delete.enable=false"
  })
  @EnabledIfEnvironmentVariable(disabledReason = "Long running tests are disabled", named = "CI", matches = "true")
  @Nested
  class ArchiveTest {
    @Test
    void shouldArchive() {
      // We create a proposal which has been proposed two days ago
      var proposal = getSampleProposal(ProposalStatus.PROPOSED, Instant.now().minus(Duration.ofDays(2)));
      proposal = proposalRepository.save(proposal);

      final var proposalId = proposal.getId();
      // Cron is configured to run every second
      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> proposalHasStatus(proposalId, ProposalStatus.ARCHIVED));
    }
  }

  @SpringBootTest(properties = {
      "project-service.proposals.jobs.auto-archive.enable=false",
      "project-service.proposals.jobs.auto-mark-for-delete.enable=true",
      "project-service.proposals.jobs.auto-mark-for-delete.after=P2D",
      "project-service.proposals.jobs.auto-mark-for-delete.cron=* * * * * *",
      "project-service.proposals.jobs.auto-delete.enable=false"
  })
  @EnabledIfEnvironmentVariable(disabledReason = "Long running tests are disabled", named = "CI", matches = "true")
  @Nested
  class MarkTest {
    @Test
    void shouldMark() {
      // We create a proposal which has been proposed two days ago
      var proposal = getSampleProposal(ProposalStatus.ARCHIVED, Instant.now().minus(Duration.ofDays(2)));
      proposal = proposalRepository.save(proposal);

      final var proposalId = proposal.getId();
      // Cron is configured to run every second
      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> proposalHasStatus(proposalId, ProposalStatus.READY_FOR_DELETION));
    }
  }

  @SpringBootTest(properties = {
      "project-service.proposals.jobs.auto-archive.enable=false",
      "project-service.proposals.jobs.auto-mark-for-delete.enable=false",
      "project-service.proposals.jobs.auto-delete.enable=true",
      "project-service.proposals.jobs.auto-delete.cron=* * * * * *",
  })
  @EnabledIfEnvironmentVariable(disabledReason = "Long running tests are disabled", named = "CI", matches = "true")
  @Nested
  class DeleteTest {
    @Test
    void shouldDelete() {
      // We create a proposal which has been proposed two days ago
      var proposal = getSampleProposal(ProposalStatus.READY_FOR_DELETION, Instant.now());
      proposal = proposalRepository.save(proposal);

      final var proposalId = proposal.getId();
      // Cron is configured to run every second
      await().atMost(Duration.ofSeconds(2)).untilAsserted(() -> assertThat(proposalRepository.existsById(proposalId)).isFalse());
    }
  }

  @ParameterizedTest(name = "should not touch proposals with a newer timestamp")
  @EnumSource(value = ProposalStatus.class, names = { "ARCHIVED", "PROPOSED" })
  void shouldNotTouchProposalsWithNewerTimestamp(ProposalStatus status)
      throws InterruptedException {
    // We create a proposal which has been proposed two days ago
    var proposal = getSampleProposal(status, Instant.now());
    proposal = proposalRepository.save(proposal);

    final var proposalId = proposal.getId();
    // Cron is configured to run every minute, so it should be sufficient to wait 2 seconds.
    // This time we can sleep, because we want to make sure 2 seconds are over
    Thread.sleep(2000);
    proposalHasStatus(proposalId, status);
  }

  void proposalHasStatus(UUID proposalId, ProposalStatus status) {
    assertThat(proposalRepository.findById(proposalId))
        .isPresent()
        .get()
        .extracting(Proposal::getStatus)
        .isEqualTo(status);
  }
}
