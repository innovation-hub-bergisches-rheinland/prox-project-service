package de.innovationhub.prox.projectservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.lang.Nullable;
import java.util.UUID;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrganizationRequestContextAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
  private final UserInfoRequestHeaderExtractor userInfoExtractor;
  private final static String ORG_ID_VARIABLE = "orgId";

  public OrganizationRequestContextAuthorizationManager() {
    this.userInfoExtractor = new UserInfoRequestHeaderExtractor();
  }

  public OrganizationRequestContextAuthorizationManager(UserInfoRequestHeaderExtractor userInfoExtractor) {
    this.userInfoExtractor = userInfoExtractor;
  }


  @Override
  public AuthorizationDecision check(Supplier<Authentication> authentication,
      RequestAuthorizationContext object) {
    // If we don't have a orgId in the request context, we don't need to do that header parsing below
    var value = object.getVariables().get(ORG_ID_VARIABLE);
    if(value == null) {
      log.warn("No authorization decision could be made because the request variable '{}' is not present", ORG_ID_VARIABLE);
      return null;
    }

    try {
      var orgId = UUID.fromString(value);
      var userInfo = userInfoExtractor.parseUserInfoFromRequest(object.getRequest());

      if (userInfo == null) {
        return null;
      }

      return new AuthorizationDecision(userInfo.orgs().contains(orgId));

    } catch (IllegalArgumentException e) {
      log.error("Value of request variable '{}' is not a UUID compliant string. Actual: '{}'", ORG_ID_VARIABLE, value);
      throw e;
    }
  }
}
