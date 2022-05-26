package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class ProjectPermissionEvaluatorHelperTest {
  private final UserInfoRequestHeaderExtractor userInfoRequestHeaderExtractor =
      mock(UserInfoRequestHeaderExtractor.class);
  private final ProjectPermissionEvaluatorHelper helper =
      new ProjectPermissionEvaluatorHelper(userInfoRequestHeaderExtractor);

  @Test
  void shouldReturnFalseWhenNotAuthenticated() {
    var project = getTestProject(UUID.randomUUID());
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
    var project = getTestProject(user);
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermission(project, auth, new UserInfo(user, Collections.emptySet())))
        .isTrue();
  }

  @Test
  void shouldReturnFalseWhenUserIsNotOwner() {
    var user = UUID.randomUUID();
    var project = getTestProject(UUID.randomUUID());
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
    var project = getTestOrgProject(orgId);
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
    var project = getTestOrgProject(orgId);
    var auth = mock(Authentication.class);

    when(auth.isAuthenticated()).thenReturn(true);
    when(auth.getName()).thenReturn(user.toString());

    assertThat(helper.hasPermission(project, auth, new UserInfo(user, Set.of()))).isFalse();
  }

  private Project getTestProject(UUID userId) {
    return new Project(
        "Test Project",
        "Test Project Description",
        "Test Project Short Description",
        "Test Project Requirement",
        ProjectStatus.AVAILABLE,
        "Test Project Creator Name",
        "Test Project Supervisor",
        Collections.emptySet(),
        Collections.emptySet(),
        new User(userId),
        Instant.now(),
        Instant.now());
  }

  private Project getTestOrgProject(UUID orgId) {
    return new Project(
        "Test Project",
        "Test Project Description",
        "Test Project Short Description",
        "Test Project Requirement",
        ProjectStatus.AVAILABLE,
        "Test Project Creator Name",
        "Test Project Supervisor",
        Collections.emptySet(),
        Collections.emptySet(),
        new Organization(orgId),
        Instant.now(),
        Instant.now());
  }
}
