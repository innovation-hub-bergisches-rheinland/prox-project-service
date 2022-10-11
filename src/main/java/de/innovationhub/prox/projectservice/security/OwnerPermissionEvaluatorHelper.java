package de.innovationhub.prox.projectservice.security;


import de.innovationhub.prox.projectservice.owners.AbstractOwnerRepository;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
public class OwnerPermissionEvaluatorHelper {

  private final AbstractOwnerRepository abstractOwnerRepository;

  public OwnerPermissionEvaluatorHelper(AbstractOwnerRepository abstractOwnerRepository) {
    this.abstractOwnerRepository = abstractOwnerRepository;
  }

  @Transactional
  public boolean hasPermissionWithOwnerId(UUID ownerId, Authentication authentication) {
    if (!authentication.isAuthenticated()) {
      return false;
    }

    var optOwner = abstractOwnerRepository.findById(ownerId);

    if(optOwner.isEmpty()) {
      log.error("Could not find owner with ID '{}'", ownerId);
      return false;
    }

    var owner = optOwner.get();
    var discriminator = owner.getDiscriminator();

    // If a user is the owner, we will check whether the actual user is authenticated
    if (discriminator.equals(User.DISCRIMINATOR)) {
      return authentication.getName().equals(owner.getId().toString());
    }

    // If an organization is owner, we will check whether the authenticated user is member of that
    // org.
    if (discriminator.equals(Organization.DISCRIMINATOR) && owner instanceof Organization organization) {
      return organization.getMembers().contains(UUID.fromString(authentication.getName()));
    }

    log.error("Cannot decide because the discriminator {} is unknown", discriminator);
    return false;
  }

  public boolean hasPermissionWithCurrentContext(UUID ownerId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return hasPermissionWithOwnerId(ownerId, authentication);
  }
}
