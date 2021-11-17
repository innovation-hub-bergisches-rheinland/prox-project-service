package de.innovationhub.prox.projectservice.utils;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

/** Provides some Authentication Utilities */
public interface AuthenticationUtils {
  /**
   * Method to obtain UUID of the requesting user from a HttpServletRequest
   *
   * @param request the request of which the UUID should be extracted
   * @return If extraction was successful it returns the UUID, otherwise a empty optional
   */
  Optional<UUID> getUserUUIDFromRequest(HttpServletRequest request);

  /**
   * Checks whether the authenticated user is in role
   * @param role
   * @return
   */
  default boolean authenticatedUserIsInRole(String role) {
    var roleMatch = role.trim().startsWith("ROLE_") ? role.trim() : "ROLE_" + role.trim();
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication != null) {
      return authentication
          .getAuthorities()
          .stream()
          .anyMatch(r -> r.getAuthority().equalsIgnoreCase(roleMatch));
    }
    return false;
  }
}
