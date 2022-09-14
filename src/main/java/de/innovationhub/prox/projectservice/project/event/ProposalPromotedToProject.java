package de.innovationhub.prox.projectservice.project.event;

import java.util.UUID;

public record ProposalPromotedToProject(
  UUID proposalId,
  UUID projectId
) {

}
