package de.innovationhub.prox.projectservice.project.dto;

import de.innovationhub.prox.projectservice.proposal.Proposal;

public record CreateProjectFromProposal(
  Proposal proposal
) {

}
