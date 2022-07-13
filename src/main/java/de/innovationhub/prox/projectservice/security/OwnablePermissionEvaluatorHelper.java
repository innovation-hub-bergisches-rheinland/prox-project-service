package de.innovationhub.prox.projectservice.security;


import de.innovationhub.prox.projectservice.core.Ownable;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
public class OwnablePermissionEvaluatorHelper<T extends Ownable> {
  private final UserInfoRequestHeaderExtractor requestHeaderExtractor;

  public OwnablePermissionEvaluatorHelper(UserInfoRequestHeaderExtractor extractor) {
    this.requestHeaderExtractor = extractor;
  }

  public boolean hasPermission(T ownable, Authentication authentication, UserInfo userInfo) {
    if (!authentication.isAuthenticated()) {
      return false;
    }

    var owner = ownable.getOwner();
    var discriminator = owner.getDiscriminator();

    if (discriminator.equals(User.DISCRIMINATOR)) {
      return authentication.getName().equals(ownable.getOwner().getId().toString());
    }

    if (discriminator.equals(Organization.DISCRIMINATOR)) {
      if (userInfo == null) {
        return false;
      }
      return userInfo.orgs().stream().anyMatch(oId -> owner.getId().equals(oId));
    }

    log.error("Cannot decide because the discriminator {} is unknown", discriminator);
    return false;
  }

  public boolean hasPermission(
      T ownable, Authentication authentication, HttpServletRequest request) {
    var userInfo = requestHeaderExtractor.parseUserInfoFromRequest(request);
    return hasPermission(ownable, authentication, userInfo);
  }

  public boolean hasPermissionWithCurrentContext(T ownable) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var optRequest = getCurrentHttpRequest();
    if (optRequest.isEmpty()) {
      log.debug("No Request present");
      return false;
    }
    return hasPermission(ownable, authentication, optRequest.get());
  }

  private Optional<HttpServletRequest> getCurrentHttpRequest() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast)
        .map(ServletRequestAttributes::getRequest);
  }
}
