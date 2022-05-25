package de.innovationhub.prox.projectservice.security;

import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.project.ProjectRepository;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectRequestContextAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
  private final static String PROJECT_ID_VARIABLE = "projectId";
  private final ProjectRepository projectRepository;

  public ProjectRequestContextAuthorizationManager(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Override
  public AuthorizationDecision check(Supplier<Authentication> authentication,
      RequestAuthorizationContext object) {
    // If we don't have a projectId in the request context, we can't do anything
    var value = object.getVariables().get(PROJECT_ID_VARIABLE);
    if(value == null || value.isBlank()) {
      log.warn("No authorization decision could be made because the request variable '{}' is not present",
          PROJECT_ID_VARIABLE);
      return null;
    }

    var optProject = projectRepository.findById(UUID.fromString(value));
    if(optProject.isEmpty()) {
      // Can't decide if a project doesn't exist
      return null;
    }

    var project = optProject.get();

    if(project.getOwner() instanceof User) {
      try {
        var auth = authentication.get();
        if (!auth.isAuthenticated()) {
          return new AuthorizationDecision(false);
        }
        var keycloakAuth = (KeycloakAuthenticationToken) auth;
        var subjectString = keycloakAuth.getAccount().getKeycloakSecurityContext().getToken().getSubject();
        // Not necessary to parse into a UUID. We can just rely on plain string equality
        return new AuthorizationDecision(project.getOwner().getId().toString().equals(subjectString));
      } catch (ClassCastException e) {
        log.error("Could not parse the provided authentication to Keycloak token", e);
        return null;
      }
    }

    throw new RuntimeException("Not supported yet");
  }
}
