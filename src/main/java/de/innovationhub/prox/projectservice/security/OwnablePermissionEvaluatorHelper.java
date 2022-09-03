package de.innovationhub.prox.projectservice.security;


import de.innovationhub.prox.projectservice.core.Ownable;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OwnablePermissionEvaluatorHelper<T extends Ownable> {

  private final OrganizationRepository organizationRepository;

  public OwnablePermissionEvaluatorHelper(OrganizationRepository organizationRepository) {
    this.organizationRepository = organizationRepository;
  }

  public boolean hasPermission(
    T ownable, Authentication authentication) {
    if (!authentication.isAuthenticated()) {
      return false;
    }

    var owner = ownable.getOwner();
    var discriminator = owner.getDiscriminator();

    if (discriminator.equals(User.DISCRIMINATOR)) {
      return authentication.getName().equals(ownable.getOwner().getId().toString());
    }

    if (discriminator.equals(Organization.DISCRIMINATOR)) {
      var optOrg = organizationRepository.findById(owner.getId());
      if (optOrg.isEmpty()) {
        log.error("Cannot decide because the organization {} does not exist", owner.getId());
        return false;
      }
      var organization = optOrg.get();
      return organization.getMembers().contains(UUID.fromString(authentication.getName()));
    }

    log.error("Cannot decide because the discriminator {} is unknown", discriminator);
    return false;
  }

  public boolean hasPermissionWithCurrentContext(T ownable) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return hasPermission(ownable, authentication);
  }
}
