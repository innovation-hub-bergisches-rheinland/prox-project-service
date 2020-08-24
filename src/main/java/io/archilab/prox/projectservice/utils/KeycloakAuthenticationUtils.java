package io.archilab.prox.projectservice.utils;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;

public class KeycloakAuthenticationUtils implements AuthenticationUtils {

  public Optional<UUID> getUserUUIDFromRequest(HttpServletRequest request) {
    KeycloakAuthenticationToken keycloakAuthenticationToken = getAuthentication(request);
    KeycloakSecurityContext keycloakSecurityContext = keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext();
    return getSubjectUUIDFromToken(keycloakSecurityContext.getToken());
  }

  private KeycloakAuthenticationToken getAuthentication(HttpServletRequest request) {
    return (KeycloakAuthenticationToken) request.getUserPrincipal();
  }

  /**
   * Method to extract subjects UserID from Keycloak AccessToken by
   * @param accessToken Keycloak AccessToken which contains subject
   * @return Empty Optional if the AccessToken does not contain a subject or subject is not a valid UUID
   */
  private Optional<UUID> getSubjectUUIDFromToken(AccessToken accessToken) {
    String subject = accessToken.getSubject();
    if(!subject.isBlank()) {
      try {
        return Optional.of(UUID.fromString(subject));
      } catch (IllegalArgumentException illegalArgumentException) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }
}