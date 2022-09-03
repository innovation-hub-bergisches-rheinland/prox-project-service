package de.innovationhub.prox.projectservice.proposal.jobs;


import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Instant;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    properties = {
      "project-service.proposals.jobs.auto-archive.enable=true",
      "project-service.proposals.jobs.auto-archive.after=P2D",
      "project-service.proposals.jobs.auto-archive.cron=* * * * * *",
      "project-service.proposals.jobs.auto-mark-for-delete.enable=true",
      "project-service.proposals.jobs.auto-mark-for-delete.after=P2D",
      "project-service.proposals.jobs.auto-mark-for-delete.cron=* * * * * *",
      "project-service.proposals.jobs.auto-delete.enable=true",
      "project-service.proposals.jobs.auto-delete.cron=* * * * * *",
    })
@DirtiesContext
@EnabledIfEnvironmentVariable(
  disabledReason = "Long running tests are disabled",
  named = "CI",
  matches = "true")
@ActiveProfiles("h2")
class ProposalLifecycleIntegrationTest extends BaseProposalJobsTest {
  @ParameterizedTest(name = "should not touch proposals with a newer timestamp")
  @EnumSource(
      value = ProposalStatus.class,
      names = {"ARCHIVED", "PROPOSED"})
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
}
