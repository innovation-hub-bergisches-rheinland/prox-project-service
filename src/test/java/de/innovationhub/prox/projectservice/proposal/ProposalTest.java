package de.innovationhub.prox.projectservice.proposal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ProposalTest {
  @Test
  void shouldUpdateTimestampWhenStatusChanged() throws InterruptedException {
    var proposal = Proposal.builder()
        .name("Test")
        .status(ProposalStatus.PROPOSED)
        .requirement("Test")
        .shortDescription("Test")
        .build();
    var firstStatusChangedTimestamp = Instant.from(proposal.getStatusChangedAt());

    Thread.sleep(1);

    // When
    proposal.setStatus(ProposalStatus.ARCHIVED);

    // Then
    assertThat(proposal.getStatusChangedAt())
        .isAfter(firstStatusChangedTimestamp);
  }
}
