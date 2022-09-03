package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.core.Ownable;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class OwnablePermissionEvaluatorHelperTest {

  private final OrganizationRepository organizationRepository =
    mock(OrganizationRepository.class);
  private final OwnablePermissionEvaluatorHelper<TestOwnable> helper =
    new OwnablePermissionEvaluatorHelper<>(organizationRepository);

  @Getter
  class TestOwnable implements Ownable {

    private final AbstractOwner owner;

    TestOwnable(AbstractOwner owner) {
      this.owner = owner;
    }
  }

  @Test
  void shouldReturnFalseWhenNotAuthenticated() {
    var project = getTestOwnableUser(UUID.randomUUID());
    var auth = mock(Authentication.class);
    when(auth.isAuthenticated()).thenReturn(false);

    assertThat(
      helper.hasPermission(
        project, auth))
        .isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserIsOwner() {
    var user = UUID.randomUUID();
    var project = getTestOwnableUser(user);
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermission(project, auth))
        .isTrue();
  }

  @Test
  void shouldReturnFalseWhenUserIsNotOwner() {
    var user = UUID.randomUUID();
    var project = getTestOwnableUser(UUID.randomUUID());
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermission(project, auth))
        .isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserIsInOrg() {
    var user = UUID.randomUUID();
    var orgId = UUID.randomUUID();
    var project = getTestOwnableOrg(orgId);
    var org = new Organization(orgId, "ACME Ltd");
    org.setMembers(Set.of(user));
    when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));

    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(
      helper.hasPermission(
        project, auth))
      .isTrue();
    verify(organizationRepository).findById(eq(orgId));
  }

  @Test
  void shouldReturnFalseWhenUserIsNotInOrg() {
    var user = UUID.randomUUID();
    var orgId = UUID.randomUUID();
    var project = getTestOwnableOrg(orgId);
    var org = new Organization(orgId, "ACME Ltd");
    when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermission(project, auth)).isFalse();
  }

  private TestOwnable getTestOwnableUser(UUID userId) {
    return new TestOwnable(new User(userId, "Xavier Tester"));
  }

  private TestOwnable getTestOwnableOrg(UUID orgId) {
    return new TestOwnable(new Organization(orgId, "ACME Ltd."));
  }
}
