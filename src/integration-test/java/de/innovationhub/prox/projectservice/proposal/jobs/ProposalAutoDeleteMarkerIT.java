package de.innovationhub.prox.projectservice.proposal.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"project-service.proposals.jobs.auto-archive.enable=false",
  "project-service.proposals.jobs.auto-mark-for-delete.enable=true",
  "project-service.proposals.jobs.auto-mark-for-delete.after=P2D",
  "project-service.proposals.jobs.auto-mark-for-delete.cron=* * * * * *",
  "project-service.proposals.jobs.auto-delete.enable=false"})
class ProposalAutoDeleteMarkerIT extends BaseProposalJobsIT {
  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  ProposalAutoDeletionMarker autoDeletionMarker;

  @Test
  void shouldMark() {
    // We create a proposal which has been proposed two days ago
    var proposal =
      getSampleProposal(ProposalStatus.ARCHIVED, Instant.now().minus(Duration.ofDays(2)));
    proposal = proposalRepository.save(proposal);

    final var proposalId = proposal.getId();
    // Cron is configured to run every second
    await()
      .atMost(Duration.ofSeconds(5))
      .untilAsserted(() -> proposalHasStatus(proposalId, ProposalStatus.READY_FOR_DELETION));
  }

  @Test
  void shouldNotMarkProposalsNewerThanConfigured() {
    // Given
    var proposal = getSampleProposal(ProposalStatus.ARCHIVED, Instant.now());
    proposalRepository.save(proposal);

    // When
    this.autoDeletionMarker.autoMark();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal).isPresent().get().isEqualTo(proposal);
  }

  @Test
  void shouldMarkSingleProposal() {
    // Given
    var olderTimestamp = Instant.now().minus(Duration.ofDays(2));
    var proposal = getSampleProposal(ProposalStatus.ARCHIVED, olderTimestamp);
    proposalRepository.save(proposal);

    // When
    this.autoDeletionMarker.autoMark();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal)
        .isPresent()
        .get()
        .extracting(Proposal::getStatus)
        .isEqualTo(ProposalStatus.READY_FOR_DELETION);
  }

  @ParameterizedTest(name = "should not delete {0}")
  @EnumSource(
      value = ProposalStatus.class,
      names = {"PROPOSED", "READY_FOR_DELETION"})
  void shouldNotModifyProposalsWithOtherState(ProposalStatus proposalStatus) {
    // Given
    var olderTimestamp = Instant.now().minus(Duration.ofDays(2));
    var proposal = getSampleProposal(proposalStatus, olderTimestamp);
    proposalRepository.save(proposal);

    // When
    this.autoDeletionMarker.autoMark();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal).isPresent().get().isEqualTo(proposal);
  }
}
