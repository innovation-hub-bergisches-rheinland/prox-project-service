package de.innovationhub.prox.projectservice.security;


import java.util.Optional;
import java.util.UUID;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomAuditorAware implements AuditorAware<UUID> {

  @Override
  public Optional<UUID> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .map(KeycloakAuthenticationToken.class::cast)
        .map(token -> token.getPrincipal())
        .map(KeycloakPrincipal.class::cast)
        .map(
            keycloakPrincipal ->
                keycloakPrincipal.getKeycloakSecurityContext().getToken().getSubject())
        .map(UUID::fromString);
  }
}
