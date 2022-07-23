package de.innovationhub.prox.projectservice.proposal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Authentication does not have a username")
public class NoUsernameInAuthenticationException extends RuntimeException {

  public NoUsernameInAuthenticationException() {
    super();
  }
}
