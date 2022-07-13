package de.innovationhub.prox.projectservice.proposal.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.ProjectService;
import de.innovationhub.prox.projectservice.config.ProposalConfig;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(properties = {
    "project-service.proposals.jobs.auto-archive.after=P2D"
})
class ProposalAutoArchiverTest extends BaseProposalJobsTest {
  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  ProposalAutoArchiver autoArchiver;

  @Test
  void shouldNotArchiveProposalsNewerThanConfigured() {
    // Given
    var proposal = getSampleProposal(ProposalStatus.PROPOSED, Instant.now());
    proposalRepository.save(proposal);

    // When
    this.autoArchiver.autoArchive();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal)
        .isPresent()
        .get()
        .isEqualTo(proposal);
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
  @EnumSource(value = ProposalStatus.class, names = { "ARCHIVED", "READY_FOR_DELETION" })
  void shouldNotModifyProposalsWithOtherState(ProposalStatus proposalStatus) {
    // Given
    var olderTimestamp = Instant.now().minus(Duration.ofDays(2));
    var proposal = getSampleProposal(proposalStatus, olderTimestamp);
    proposalRepository.save(proposal);

    // When
    this.autoArchiver.autoArchive();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal)
        .isPresent()
        .get()
        .isEqualTo(proposal);
  }
}
