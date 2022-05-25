package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectRepository;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

class ProjectRequestContextAuthorizationManagerTest {

  private final ProjectRepository projectRepository = mock(ProjectRepository.class);
  private final UserInfoRequestHeaderExtractor userInfoRequestHeaderExtractor = mock(UserInfoRequestHeaderExtractor.class);
  private final ProjectRequestContextAuthorizationManager manager = new ProjectRequestContextAuthorizationManager(projectRepository,
      userInfoRequestHeaderExtractor);

  @Test
  void shouldReturnNullWhenNoVariableIsSupplied() {
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of());
    Supplier<Authentication> mockAuthSupplier = () -> mock(Authentication.class);

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
  }

  @Test
  void shouldReturnNullWhenAuthenticationIsNotAKeycloakToken() {
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("projectId", UUID.randomUUID().toString()));
    var mockedAuth = mock(Authentication.class);
    when(mockedAuth.isAuthenticated()).thenReturn(true);
    Supplier<Authentication> mockAuthSupplier = () -> mockedAuth;

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
  }

  @Test
  void shouldReturnNullWhenProjectNotExists() {
    var projectId = UUID.randomUUID();
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("projectId", projectId.toString()));

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
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("projectId", project.getId().toString()));
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
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("projectId", project.getId().toString()));
    Supplier<Authentication> keycloakAuthSupplier = () -> buildKeycloakToken(userId.toString());

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isTrue();
    verify(projectRepository).findById(project.getId());
  }

  @Test
  void shouldReturnFalseWhenUserIsNotProjectOwner() {
    var userId = UUID.randomUUID();
    var project = getTestProject(UUID.randomUUID());
    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("projectId", project.getId().toString()));
    Supplier<Authentication> keycloakAuthSupplier = () -> buildKeycloakToken(userId.toString());

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isFalse();
    verify(projectRepository).findById(project.getId());
  }

  @Test
  void shouldReturnTrueWhenUserIsInOrg() {
    var orgId = UUID.randomUUID();
    var project = getTestOrgProject(orgId);
    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("projectId", project.getId().toString()));
    Supplier<Authentication> keycloakAuthSupplier = () -> buildKeycloakToken(UUID.randomUUID().toString());
    when(userInfoRequestHeaderExtractor.parseUserInfoFromRequest(any())).thenReturn(new UserInfo(UUID.randomUUID(),
        Set.of(UUID.randomUUID(), orgId)));

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isTrue();
    verify(projectRepository).findById(project.getId());
  }

  @Test
  void shouldReturnFalseWhenUserIsNotInOrg() {
    var orgId = UUID.randomUUID();
    var project = getTestOrgProject(orgId);
    when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("projectId", project.getId().toString()));
    Supplier<Authentication> keycloakAuthSupplier = () -> buildKeycloakToken(UUID.randomUUID().toString());
    when(userInfoRequestHeaderExtractor.parseUserInfoFromRequest(any())).thenReturn(new UserInfo(UUID.randomUUID(),
        Set.of(UUID.randomUUID(), UUID.randomUUID())));

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isFalse();
    verify(projectRepository).findById(project.getId());
  }

  private KeycloakAuthenticationToken buildKeycloakToken(String subject) {
    var token = new AccessToken();
    token.setSubject(subject);
    final RefreshableKeycloakSecurityContext securityContext =
        new RefreshableKeycloakSecurityContext(
            null,
            null,
            "test.keycloak.token",
            token,
            "test.keycloak.token",
            token,
            null);

    final KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = new KeycloakPrincipal<>("test-user", securityContext);

    final SimpleKeycloakAccount account = new SimpleKeycloakAccount(principal, Collections.emptySet(), securityContext);

    return new KeycloakAuthenticationToken(account, false, Collections.emptySet());
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
        Instant.now()
    );
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
        Instant.now()
    );
  }
}
