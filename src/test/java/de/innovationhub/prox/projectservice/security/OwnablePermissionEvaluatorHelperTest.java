package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.core.Ownable;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class OwnablePermissionEvaluatorHelperTest {
  private final UserInfoRequestHeaderExtractor userInfoRequestHeaderExtractor =
      mock(UserInfoRequestHeaderExtractor.class);
  private final OwnablePermissionEvaluatorHelper<TestOwnable> helper =
      new OwnablePermissionEvaluatorHelper<>(userInfoRequestHeaderExtractor);

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
                project, auth, new UserInfo(UUID.randomUUID(), Collections.emptySet())))
        .isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserIsOwner() {
    var user = UUID.randomUUID();
    var project = getTestOwnableUser(user);
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermission(project, auth, new UserInfo(user, Collections.emptySet())))
        .isTrue();
  }

  @Test
  void shouldReturnFalseWhenUserIsNotOwner() {
    var user = UUID.randomUUID();
    var project = getTestOwnableUser(UUID.randomUUID());
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermission(project, auth, new UserInfo(user, Collections.emptySet())))
        .isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserIsInOrg() {
    var user = UUID.randomUUID();
    var orgId = UUID.randomUUID();
    var project = getTestOwnableOrg(orgId);
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(
            helper.hasPermission(
                project, auth, new UserInfo(user, Set.of(UUID.randomUUID(), orgId))))
        .isTrue();
  }

  @Test
  void shouldReturnFalseWhenUserIsNotInOrg() {
    var user = UUID.randomUUID();
    var orgId = UUID.randomUUID();
    var project = getTestOwnableOrg(orgId);
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermission(project, auth, new UserInfo(user, Set.of()))).isFalse();
  }

  private TestOwnable getTestOwnableUser(UUID userId) {
    return new TestOwnable(new User(userId, "Xavier Tester"));
  }

  private TestOwnable getTestOwnableOrg(UUID orgId) {
    return new TestOwnable(new Organization(orgId, "ACME Ltd."));
  }
}
