package de.innovationhub.prox.projectservice.proposal.jobs;

import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseProposalJobsTest {

  @Autowired
  UserRepository userRepository;

  Proposal getSampleProposal(ProposalStatus status, Instant lastModifiedDate) {
    var user = userRepository.save(new User(UUID.randomUUID()));

    return Proposal.builder()
        .owner(user)
        .shortDescription("Test")
        .name("Test")
        .description("Test")
        .requirement("Test")
        .statusChangedAt(lastModifiedDate)
        .status(status)
        .build();
  }
}
