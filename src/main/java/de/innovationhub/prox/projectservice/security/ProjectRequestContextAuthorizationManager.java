package de.innovationhub.prox.projectservice.security;

import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectRepository;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
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
  private final UserInfoRequestHeaderExtractor userInfoRequestHeaderExtractor;

  public ProjectRequestContextAuthorizationManager(ProjectRepository projectRepository,
      UserInfoRequestHeaderExtractor userInfoRequestHeaderExtractor) {
    this.projectRepository = projectRepository;
    this.userInfoRequestHeaderExtractor = userInfoRequestHeaderExtractor;
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

    var auth = authentication.get();
    if (!auth.isAuthenticated()) {
      return new AuthorizationDecision(false);
    }

    var optProject = projectRepository.findById(UUID.fromString(value));
    if(optProject.isEmpty()) {
      // Can't decide if a project doesn't exist
      return null;
    }

    var project = optProject.get();

    var discriminator = project.getOwner().getDiscriminator();
    switch (discriminator) {
      case "user":
        return authorizeUser(auth, project, object);
      case "organization":
        return authorizeOrganization(auth, project, object);
    }

    log.warn("Authorization decision cannot be done because '{}' is not a known discriminator value", discriminator);
    return null;
  }

  private AuthorizationDecision authorizeUser(Authentication authentication, Project project, RequestAuthorizationContext object) {
    try {
      var principal = authentication.getName();
      // Not necessary to parse into a UUID. We can just rely on plain string equality
      return new AuthorizationDecision(project.getOwner().getId().toString().equals(principal));
    } catch (ClassCastException e) {
      log.error("Could not parse the provided authentication to Keycloak token", e);
      return new AuthorizationDecision(false);
    }
  }

  private AuthorizationDecision authorizeOrganization(Authentication authentication, Project project, RequestAuthorizationContext object) {
    var userInfo = userInfoRequestHeaderExtractor.parseUserInfoFromRequest(object.getRequest());
    if(userInfo == null) {
      return new AuthorizationDecision(false);
    }
    return new AuthorizationDecision(userInfo.orgs().contains(project.getOwner().getId()));
  }
}
