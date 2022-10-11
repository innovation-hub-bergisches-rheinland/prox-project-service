package de.innovationhub.prox.projectservice.proposal.jobs;


import static org.awaitility.Awaitility.await;

import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;

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
class ProposalLifecycleIntegrationIT extends BaseProposalJobsIT {
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
    await()
      .atMost(Duration.ofSeconds(5))
      .untilAsserted(() -> proposalHasStatus(proposalId, status));
  }
}
