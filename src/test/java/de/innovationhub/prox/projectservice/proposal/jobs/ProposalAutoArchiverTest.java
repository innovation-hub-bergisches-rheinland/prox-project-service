package de.innovationhub.prox.projectservice.proposal.jobs;

import de.innovationhub.prox.projectservice.config.ProposalConfig;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// TODO: Proper test configuration
@SpringBootTest
class ProposalAutoArchiverTest {
  @Autowired
  ProposalRepository proposalRepository;
  @Autowired
  ProposalAutoArchiver proposalAutoArchiver;
  @Autowired
  ProposalConfig proposalConfig;

  @Test
  void shouldNotArchiveProposalsNewerThanConfigured() {
    // TODO
  }

  Proposal getSampleProposal(ProposalStatus status, Instant lastModifiedDate) {
    return Proposal.builder()
        .owner(new User(UUID.randomUUID()))
        .shortDescription("Test")
        .name("Test")
        .description("Test")
        .requirement("Test")
        .statusChangedAt(lastModifiedDate)
        .status(status)
        .build();
  }
}
