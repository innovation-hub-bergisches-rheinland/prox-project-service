package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.core.Ownable;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.AbstractOwnerRepository;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class OwnerPermissionEvaluatorHelperTest {

  private final AbstractOwnerRepository abstractOwnerRepository =
    mock(AbstractOwnerRepository.class);
  private final OwnerPermissionEvaluatorHelper helper = new OwnerPermissionEvaluatorHelper(abstractOwnerRepository);

  @Test
  void shouldReturnFalseWhenNotAuthenticated() {
    var auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(false);

    assertThat(
      helper.hasPermissionWithOwnerId(
        UUID.randomUUID(), auth))
        .isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserIsOwner() {
    var userId = UUID.randomUUID();
    var user = new User(userId, "Xavier Tester");
    var auth = mock(Authentication.class);

    when(abstractOwnerRepository.findById(userId)).thenReturn(Optional.of(user));
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(userId.toString());

    assertThat(helper.hasPermissionWithOwnerId(userId, auth))
        .isTrue();
  }

  @Test
  void shouldReturnFalseWhenUserIsNotOwner() {
    var userId = UUID.randomUUID();
    var user = new User(userId, "Xavier Tester");
    var auth = mock(Authentication.class);

    when(abstractOwnerRepository.findById(userId)).thenReturn(Optional.of(user));
    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(userId.toString());

    assertThat(helper.hasPermissionWithOwnerId(UUID.randomUUID(), auth))
        .isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserIsInOrg() {
    var user = UUID.randomUUID();
    var orgId = UUID.randomUUID();
    var org = new Organization(orgId, "ACME Ltd");
    org.setMembers(Set.of(user));
    when(abstractOwnerRepository.findById(orgId)).thenReturn(Optional.of(org));

    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(
      helper.hasPermissionWithOwnerId(
        orgId, auth))
      .isTrue();
    verify(abstractOwnerRepository).findById(eq(orgId));
  }

  @Test
  void shouldReturnFalseWhenUserIsNotInOrg() {
    var user = UUID.randomUUID();
    var orgId = UUID.randomUUID();
    var org = new Organization(orgId, "ACME Ltd");
    when(abstractOwnerRepository.findById(orgId)).thenReturn(Optional.of(org));
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermissionWithOwnerId(orgId, auth)).isFalse();
  }
}
