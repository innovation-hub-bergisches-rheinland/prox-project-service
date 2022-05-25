package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.c4_soft.springaddons.security.oauth2.test.OidcTokenBuilder;
import com.c4_soft.springaddons.security.oauth2.test.keycloak.KeycloakAuthenticationTokenBuilder;
import java.util.Collections;
import java.util.Map;
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

class UserRequestContextAuthorizationManagerTest {

  private final UserRequestContextAuthorizationManager manager = new UserRequestContextAuthorizationManager();

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
        Map.of("userId", UUID.randomUUID().toString()));
    var mockedAuth = mock(Authentication.class);
    when(mockedAuth.isAuthenticated()).thenReturn(true);
    Supplier<Authentication> mockAuthSupplier = () -> mockedAuth;

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
  }

  @Test
  void shouldReturnFalseWhenUserIsNotAuthenticated() {
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("userId", UUID.randomUUID().toString()));
    var mockedAuth = mock(Authentication.class);
    when(mockedAuth.isAuthenticated()).thenReturn(false);
    Supplier<Authentication> mockAuthSupplier = () -> mockedAuth;

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result.isGranted()).isFalse();
  }

  @Test
  void shouldReturnTrueWhenUserIsEqualToVariable() {
    var userId = UUID.randomUUID();
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("userId", userId.toString()));
    Supplier<Authentication> keycloakAuthSupplier = () -> buildKeycloakToken(userId.toString());

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isTrue();
  }

  @Test
  void shouldReturnFalseWhenUserIsNotEqualToVariable() {
    var userId = UUID.randomUUID();
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("userId", UUID.randomUUID().toString()));
    Supplier<Authentication> keycloakAuthSupplier = () -> buildKeycloakToken(userId.toString());

    var result = manager.check(keycloakAuthSupplier, context);

    assertThat(result.isGranted()).isFalse();
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
}
