package de.innovationhub.prox.projectservice.proposal.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseProposalJobsTest {

  @Autowired UserRepository userRepository;

  @Autowired ProposalRepository proposalRepository;

  @AfterEach
  void cleanUp() {
    proposalRepository.deleteAll();
  }

  Proposal getSampleProposal(ProposalStatus status, Instant lastModifiedDate) {
    var user = userRepository.save(new User(UUID.randomUUID()));

    return Proposal.builder()
        .owner(user)
        .name("Test")
        .description("Test")
        .requirement("Test")
        .statusChangedAt(lastModifiedDate)
        .status(status)
        .build();
  }

  void proposalHasStatus(UUID proposalId, ProposalStatus status) {
    assertThat(proposalRepository.findById(proposalId))
        .isPresent()
        .get()
        .extracting(Proposal::getStatus)
        .isEqualTo(status);
  }
}
