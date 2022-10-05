package de.innovationhub.prox.projectservice.proposal.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import de.innovationhub.prox.projectservice.proposal.jobs.ProposalAutoArchiverTest.ArchiveTest.ArchiveTestContextInitializer;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;

@SpringBootTest(properties = {"project-service.proposals.jobs.auto-archive.after=P2D"})
@ActiveProfiles("h2")
class ProposalAutoArchiverTest extends BaseProposalJobsTest {
  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  ProposalAutoArchiver autoArchiver;

  @ContextConfiguration(initializers = {ArchiveTestContextInitializer.class})
  @EnabledIfEnvironmentVariable(
      disabledReason = "Long running tests are disabled",
      named = "CI",
      matches = "true")
  @Nested
  class ArchiveTest {

    static class ArchiveTestContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
      @Override
      public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            configurableApplicationContext,
            "project-service.proposals.jobs.auto-archive.enable=true",
            "project-service.proposals.jobs.auto-archive.after=P2D",
            "project-service.proposals.jobs.auto-archive.cron=* * * * * *",
            "project-service.proposals.jobs.auto-mark-for-delete.enable=false",
            "project-service.proposals.jobs.auto-delete.enable=false");
      }
    }

    @Test
    void shouldArchive() {
      // We create a proposal which has been proposed two days ago
      var proposal =
          getSampleProposal(ProposalStatus.PROPOSED, Instant.now().minus(Duration.ofDays(2)));
      proposal = proposalRepository.save(proposal);

      final var proposalId = proposal.getId();
      // Cron is configured to run every second
      await()
          .atMost(Duration.ofSeconds(2))
          .untilAsserted(() -> proposalHasStatus(proposalId, ProposalStatus.ARCHIVED));
    }
  }

  @Test
  void shouldNotArchiveProposalsNewerThanConfigured() {
    // Given
    var proposal = getSampleProposal(ProposalStatus.PROPOSED, Instant.now());
    proposalRepository.save(proposal);

    // When
    this.autoArchiver.autoArchive();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal).isPresent().get().isEqualTo(proposal);
  }

  @Test
  void shouldArchiveSingleProposal() {
    // Given
    var olderTimestamp = Instant.now().minus(Duration.ofDays(2));
    var proposal = getSampleProposal(ProposalStatus.PROPOSED, olderTimestamp);
    proposalRepository.save(proposal);

    // When
    this.autoArchiver.autoArchive();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal)
        .isPresent()
        .get()
        .extracting(Proposal::getStatus)
        .isEqualTo(ProposalStatus.ARCHIVED);
  }

  @ParameterizedTest(name = "should not delete {0}")
  @EnumSource(
      value = ProposalStatus.class,
      names = {"ARCHIVED", "READY_FOR_DELETION"})
  void shouldNotModifyProposalsWithOtherState(ProposalStatus proposalStatus) {
    // Given
    var olderTimestamp = Instant.now().minus(Duration.ofDays(2));
    var proposal = getSampleProposal(proposalStatus, olderTimestamp);
    proposalRepository.save(proposal);

    // When
    this.autoArchiver.autoArchive();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal).isPresent().get().isEqualTo(proposal);
  }
}
