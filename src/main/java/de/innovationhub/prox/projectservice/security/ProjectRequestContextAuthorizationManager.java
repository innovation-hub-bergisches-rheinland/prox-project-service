package de.innovationhub.prox.projectservice.security;


import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectRepository;
import java.util.UUID;
import java.util.function.Supplier;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectRequestContextAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {
  public static final String PROJECT_ID_VARIABLE = "projectId";
  private final ProjectRepository projectRepository;
  private final OwnablePermissionEvaluatorHelper<Project> permissionEvaluatorHelper;

  public ProjectRequestContextAuthorizationManager(
      ProjectRepository projectRepository,
      OwnablePermissionEvaluatorHelper<Project> permissionEvaluatorHelper) {
    this.projectRepository = projectRepository;
    this.permissionEvaluatorHelper = permissionEvaluatorHelper;
  }

  @Override
  @Transactional
  public AuthorizationDecision check(
      Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    // If we don't have a projectId in the request context, we can't do anything
    var value = object.getVariables().get(PROJECT_ID_VARIABLE);
    if (value == null || value.isBlank()) {
      log.warn(
          "No authorization decision could be made because the request variable '{}' is not present",
          PROJECT_ID_VARIABLE);
      return null;
    }

    var auth = authentication.get();
    if (!auth.isAuthenticated()) {
      return new AuthorizationDecision(false);
    }

    var optProject = projectRepository.findById(UUID.fromString(value));
    if (optProject.isEmpty()) {
      // Can't decide if a project doesn't exist
      return null;
    }

    var project = optProject.get();
    try {
      var authenticatedUserId = UUID.fromString(auth.getName());
      if (project.isSupervisor(authenticatedUserId)) {
        return new AuthorizationDecision(true);
      }
    } catch (IllegalArgumentException e) {
      log.debug("Authenticated user id is not a UUID", e);
    }

    return new AuthorizationDecision(
      permissionEvaluatorHelper.hasPermission(project, auth));
  }
}
