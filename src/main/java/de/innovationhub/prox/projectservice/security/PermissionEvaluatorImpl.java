package de.innovationhub.prox.projectservice.security;


import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectRepository;
import java.io.Serializable;
import java.util.UUID;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class PermissionEvaluatorImpl implements PermissionEvaluator {
  private final ProjectRepository projectRepository;

  public PermissionEvaluatorImpl(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Override
  public boolean hasPermission(
      Authentication authentication, Object targetDomainObject, Object permission) {
    var permissionStr = (String) permission;
    var keycloakAuth = (KeycloakAuthenticationToken) authentication;

    // Project
    if (targetDomainObject instanceof Project) {
      var project = (Project) targetDomainObject;

      if (permissionStr.equalsIgnoreCase("WRITE")) {
        return project
            .getCreatorID()
            .toString()
            .equals(keycloakAuth.getAccount().getKeycloakSecurityContext().getToken().getSubject());
      }
    }

    return false;
  }

  @Override
  public boolean hasPermission(
      Authentication authentication, Serializable targetId, String targetType, Object permission) {
    if (targetType.equals(Project.class.getSimpleName())) {
      var id = UUID.fromString((String) targetId);
      return this.projectRepository
          .findById(id)
          .map(project -> this.hasPermission(authentication, project, permission))
          .orElse(false);
    }
    return false;
  }
}
