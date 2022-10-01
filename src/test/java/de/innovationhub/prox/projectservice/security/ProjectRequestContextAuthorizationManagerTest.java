package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectRepository;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import de.innovationhub.prox.projectservice.project.Supervisor;
import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

class ProjectRequestContextAuthorizationManagerTest {

  private final ProjectRepository projectRepository = mock(ProjectRepository.class);
  private final OwnablePermissionEvaluatorHelper permissionEvaluatorHelper =
      mock(OwnablePermissionEvaluatorHelper.class);
  private final ProjectRequestContextAuthorizationManager manager =
      new ProjectRequestContextAuthorizationManager(projectRepository, permissionEvaluatorHelper);

  @Test
  void shouldReturnNullWhenNoVariableIsSupplied() {
    var context = new RequestAuthorizationContext(new MockHttpServletRequest(), Map.of());
    Supplier<Authentication> mockAuthSupplier = () -> mock(Authentication.class);

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
  }

  @Test
  void shouldReturnNullWhenProjectNotExists() {
    var projectId = UUID.randomUUID();
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    var context =
        new RequestAuthorizationContext(
            new MockHttpServletRequest(), Map.of("projectId", projectId.toString()));

    var mockedAuth = mock(Authentication.class);
    when(mockedAuth.isAuthenticated()).thenReturn(true);
    Supplier<Authentication> mockAuthSupplier = () -> mockedAuth;

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
    verify(projectRepository).findById(projectId);
  }

  @Test
  void shouldReturnFalseWhenUserIsNotAuthenticated() {
    var project = getTestProject(UUID.randomUUID());
    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
    var context =
        new RequestAuthorizationContext(
            new MockHttpServletRequest(), Map.of("projectId", project.getId().toString()));
    var mockedAuth = mock(Authentication.class);
    when(mockedAuth.isAuthenticated()).thenReturn(false);
    Supplier<Authentication> mockAuthSupplier = () -> mockedAuth;

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result.isGranted()).isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserIsProjectOwner() {
    var userId = UUID.randomUUID();
    var project = getTestProject(userId);
    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
    var context =
        new RequestAuthorizationContext(
            new MockHttpServletRequest(), Map.of("projectId", project.getId().toString()));
    when(permissionEvaluatorHelper.hasPermission(
      any(Project.class), any(Authentication.class)))
      .thenReturn(true);
    Supplier<Authentication> keycloakAuthSupplier = () -> mockedAuth(userId.toString());

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isTrue();
    verify(projectRepository).findById(project.getId());
  }

  @Test
  void shouldReturnTrueWhenUserIsSupervisor() {
    var userId = UUID.randomUUID();
    var project = getTestProject(UUID.randomUUID());
    project.setSupervisors(List.of(new Supervisor(userId, "Test")));
    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

    var context =
      new RequestAuthorizationContext(
        new MockHttpServletRequest(), Map.of("projectId", project.getId().toString()));
    Supplier<Authentication> keycloakAuthSupplier = () -> mockedAuth(userId.toString());

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isTrue();
    verify(projectRepository).findById(project.getId());
  }

  @Test
  void shouldReturnFalseWhenUserIsNotProjectOwner() {
    var userId = UUID.randomUUID();
    var project = getTestProject(UUID.randomUUID());
    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
    when(permissionEvaluatorHelper.hasPermission(
      any(Project.class), any(Authentication.class)))
      .thenReturn(false);
    var context =
        new RequestAuthorizationContext(
            new MockHttpServletRequest(), Map.of("projectId", project.getId().toString()));
    Supplier<Authentication> keycloakAuthSupplier = () -> mockedAuth(userId.toString());

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isFalse();
    verify(projectRepository).findById(project.getId());
  }

  private Authentication mockedAuth(String principalName) {
    var token = new TestingAuthenticationToken((Principal) () -> principalName, null);
    token.setAuthenticated(true);
    return token;
  }

  private Project getTestProject(UUID userId) {
    return new Project(
      "Test Project",
      "Test Project Description",
      "Test Project Short Description",
      "Test Project Requirement",
      ProjectStatus.AVAILABLE,
      "Test Project Creator Name",
      List.of(new Supervisor(UUID.randomUUID(), "Test Project Supervisor")),
      Collections.emptySet(),
      Collections.emptySet(),
      new User(userId, "Xavier Tester"),
      null,
      Collections.emptyList(),
      Instant.now(),
      Instant.now());
  }
}
