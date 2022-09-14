package de.innovationhub.prox.projectservice.proposal.event;

import java.util.UUID;

public record ProposalPromotedToProject(
  UUID proposalId,
  UUID supervisorId
) {

}
