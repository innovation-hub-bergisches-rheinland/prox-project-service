package de.innovationhub.prox.projectservice.proposal.event;

import java.util.UUID;

public record ProposalReceivedCommitment(
  UUID proposalId,
  UUID supervisorId
) {

}
