package de.innovationhub.prox.projectservice.proposal.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Authentication is not supported")
public class UnsupportedAuthenticationException extends RuntimeException {

  public UnsupportedAuthenticationException() {
    super();
  }
}
