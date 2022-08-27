package de.innovationhub.prox.projectservice.project.exception;


import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Project Not Found")
public class ProjectNotFoundException extends RuntimeException {

  public ProjectNotFoundException(UUID id) {
    super("Project with ID '%s' not found".formatted(id));
  }
}
