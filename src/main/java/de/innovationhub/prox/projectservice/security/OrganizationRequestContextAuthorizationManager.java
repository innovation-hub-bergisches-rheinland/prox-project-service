package de.innovationhub.prox.projectservice.security;


import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
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
public class OrganizationRequestContextAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {

  private static final String ORG_ID_VARIABLE = "orgId";
  private final OrganizationRepository organizationRepository;

  public OrganizationRequestContextAuthorizationManager(
    OrganizationRepository organizationRepository) {
    this.organizationRepository = organizationRepository;
  }

  @Override
  public AuthorizationDecision check(
      Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    // If we don't have a orgId in the request context, we don't need to do that header parsing
    // below
    var value = object.getVariables().get(ORG_ID_VARIABLE);
    if (value == null) {
      log.warn(
          "No authorization decision could be made because the request variable '{}' is not present",
          ORG_ID_VARIABLE);
      return null;
    }

    try {
      var orgId = UUID.fromString(value);
      var optOrg = organizationRepository.findById(orgId);
      if (optOrg.isEmpty()) {
        log.warn("No organization with id '{}' found", orgId);
        return null;
      }

      var org = optOrg.get();
      var userId = UUID.fromString(authentication.get().getName());
      return new AuthorizationDecision(org.getMembers().contains(userId));

    } catch (IllegalArgumentException e) {
      log.error(
          "Value of request variable '{}' is not a UUID compliant string. Actual: '{}'",
          ORG_ID_VARIABLE,
          value);
      throw e;
    }
  }
}
