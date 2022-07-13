package de.innovationhub.prox.projectservice.proposal.exception;

import java.util.UUID;

public class ProposalNotFoundException extends RuntimeException {

  public ProposalNotFoundException(UUID id) {
    super("Proposal with ID '%s' not found".formatted(id));
  }
}
