package de.innovationhub.prox.projectservice.proposal.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Proposal Not Found")
public class ProposalNotFoundException extends RuntimeException {

  public ProposalNotFoundException(UUID id) {
    super("Proposal with ID '%s' not found".formatted(id));
  }
}
