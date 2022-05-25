package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

class OrganizationRequestContextAuthorizationManagerTest {
  private final UserInfoRequestHeaderExtractor extractor = mock(UserInfoRequestHeaderExtractor.class);
  private final Authentication authentication = mock(Authentication.class);
  private final Supplier<Authentication> mockAuthSupplier = () -> authentication;
  private final OrganizationRequestContextAuthorizationManager manager = new OrganizationRequestContextAuthorizationManager(extractor);

  @Test
  void shouldReturnNullWhenNoVariableIsSupplied() {
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of());

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
    verify(extractor, times(0)).parseUserInfoFromRequest(any());
  }

  @Test
  void shouldReturnNullWhenExtractorReturnsNull() {
    when(extractor.parseUserInfoFromRequest(any())).thenReturn(null);
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("orgId", UUID.randomUUID().toString()));

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
    verify(extractor).parseUserInfoFromRequest(any());
  }

  @Test
  void shouldThrowWhenContextVariableIsNotAUUID() {
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("orgId", "abc"));

    assertThrows(IllegalArgumentException.class, () -> manager.check(mockAuthSupplier, context));
  }

  @Test
  void shouldReturnTrueWhenUserIsInOrg() {
    var orgId = UUID.randomUUID();
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("orgId", orgId.toString()));
    var userInfo = new UserInfo(UUID.randomUUID(), Set.of(UUID.randomUUID(), orgId));
    when(extractor.parseUserInfoFromRequest(any())).thenReturn(userInfo);

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result.isGranted()).isTrue();
    verify(extractor).parseUserInfoFromRequest(any());
  }

  @Test
  void shouldReturnFalseWhenUserIsNotInOrg() {
    var context = new RequestAuthorizationContext(
        new MockHttpServletRequest(),
        Map.of("orgId", UUID.randomUUID().toString()));
    var userInfo = new UserInfo(UUID.randomUUID(), Set.of(UUID.randomUUID()));
    when(extractor.parseUserInfoFromRequest(any())).thenReturn(userInfo);

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result.isGranted()).isFalse();
    verify(extractor).parseUserInfoFromRequest(any());
  }
}
