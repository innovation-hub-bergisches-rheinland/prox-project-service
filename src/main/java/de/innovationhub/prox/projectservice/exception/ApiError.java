package de.innovationhub.prox.projectservice.exception;


import java.util.Collections;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiError {

  private HttpStatus status;
  private List<String> errors;

  public ApiError(HttpStatus status, List<String> errors) {
    this.status = status;
    this.errors = errors;
  }

  public ApiError(HttpStatus status, String error) {
    this.status = status;
    errors = Collections.singletonList(error);
  }
}
