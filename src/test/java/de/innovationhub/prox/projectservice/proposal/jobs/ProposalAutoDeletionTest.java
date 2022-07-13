package de.innovationhub.prox.projectservice.proposal.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProposalAutoDeletionTest extends BaseProposalJobsTest {
  @Autowired
  ProposalRepository proposalRepository;

  @Autowired
  ProposalAutoDeletion autoDeletion;

  @Test
  void shouldDelete() {
    // Given
    var proposal = getSampleProposal(ProposalStatus.READY_FOR_DELETION, Instant.now());
    proposalRepository.save(proposal);

    // When
    this.autoDeletion.autoDelete();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal)
        .isEmpty();
  }

  @ParameterizedTest(name = "should not delete {0}")
  @EnumSource(value = ProposalStatus.class, names = { "ARCHIVED", "PROPOSED" })
  void shouldNotDeleteProposalInOtherState(ProposalStatus proposalStatus) {
    // Given
    var olderTimestamp = Instant.now().minus(Duration.ofDays(2));
    var proposal = getSampleProposal(proposalStatus, olderTimestamp);
    proposalRepository.save(proposal);

    // When
    this.autoDeletion.autoDelete();

    // Then
    var foundProposal = proposalRepository.findById(proposal.getId());
    assertThat(foundProposal)
        .isPresent()
        .get()
        .isEqualTo(proposal);
  }
}
