package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

class OrganizationRequestContextAuthorizationManagerTest {

  private final OrganizationRepository organizationRepository =
    mock(OrganizationRepository.class);
  private final Authentication authentication = mock(Authentication.class);
  private final Supplier<Authentication> mockAuthSupplier = () -> authentication;
  private final OrganizationRequestContextAuthorizationManager manager =
    new OrganizationRequestContextAuthorizationManager(organizationRepository);

  @Test
  void shouldReturnNullWhenNoVariableIsSupplied() {
    var context = new RequestAuthorizationContext(new MockHttpServletRequest(), Map.of());

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
  }

  @Test
  void shouldReturnNullWhenRepositoryReturnsNull() {
    when(organizationRepository.findById(any())).thenReturn(Optional.empty());
    var context =
      new RequestAuthorizationContext(
        new MockHttpServletRequest(), Map.of("orgId", UUID.randomUUID().toString()));

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result).isNull();
    verify(organizationRepository).findById(any());
  }

  @Test
  void shouldThrowWhenContextVariableIsNotAUUID() {
    var context =
        new RequestAuthorizationContext(new MockHttpServletRequest(), Map.of("orgId", "abc"));

    assertThrows(IllegalArgumentException.class, () -> manager.check(mockAuthSupplier, context));
  }

  @Test
  void shouldReturnTrueWhenUserIsInOrg() {
    var orgId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var context =
      new RequestAuthorizationContext(
        new MockHttpServletRequest(), Map.of("orgId", orgId.toString()));
    var org = new Organization(orgId, "ACME Ltd.");
    org.setMembers(Set.of(userId));
    when(organizationRepository.findById(eq(orgId))).thenReturn(Optional.of(org));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn(userId.toString());

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result.isGranted()).isTrue();
    verify(authentication).getName();
    verify(organizationRepository).findById(eq(orgId));
  }

  @Test
  void shouldReturnFalseWhenUserIsNotInOrg() {
    var orgId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var context =
      new RequestAuthorizationContext(
        new MockHttpServletRequest(), Map.of("orgId", orgId.toString()));
    var org = new Organization(orgId, "ACME Ltd.");
    when(organizationRepository.findById(eq(orgId))).thenReturn(Optional.of(org));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn(userId.toString());

    var result = manager.check(mockAuthSupplier, context);

    assertThat(result.isGranted()).isFalse();
    verify(authentication).getName();
    verify(organizationRepository).findById(eq(orgId));
  }
}
