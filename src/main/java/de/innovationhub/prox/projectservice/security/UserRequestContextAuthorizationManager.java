package de.innovationhub.prox.projectservice.security;


import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserRequestContextAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {
  private static final String User_ID_VARIABLE = "userId";

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    // If we don't have a userId in the request context, we can't do anything
    var value = object.getVariables().get(User_ID_VARIABLE);
    if (value == null || value.isBlank()) {
      log.warn(
          "No authorization decision could be made because the request variable '{}' is not present",
          User_ID_VARIABLE);
      return null;
    }

    try {
      var auth = authentication.get();
      if (!auth.isAuthenticated()) {
        return new AuthorizationDecision(false);
      }
      var principal = auth.getName();
      // Not necessary to parse into a UUID. We can just rely on plain string equality
      return new AuthorizationDecision(value.equals(principal));
    } catch (ClassCastException e) {
      log.error("Could not parse the provided authentication to Keycloak token", e);
      return new AuthorizationDecision(false);
    }
  }
}
