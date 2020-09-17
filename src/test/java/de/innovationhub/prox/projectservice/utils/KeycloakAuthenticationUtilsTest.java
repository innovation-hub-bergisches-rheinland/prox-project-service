/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.projectservice.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class KeycloakAuthenticationUtilsTest {

  private final KeycloakAuthenticationUtils keycloakAuthenticationUtils =
      new KeycloakAuthenticationUtils();

  @Mock private OidcKeycloakAccount account;

  @Mock private KeycloakAuthenticationToken keycloakAuthenticationToken;

  @Mock private KeycloakSecurityContext keycloakSecurityContext;

  @Mock private AccessToken accessToken;

  @Mock private HttpServletRequest request;

  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(keycloakAuthenticationToken.getAccount()).thenReturn(account);
    SecurityContextHolder.getContext().setAuthentication(keycloakAuthenticationToken);
    Mockito.when(account.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);
    Mockito.when(keycloakSecurityContext.getToken()).thenReturn(accessToken);
    Mockito.when(request.getUserPrincipal()).thenReturn(keycloakAuthenticationToken);
  }

  @Test
  void when_valid_uuid_in_accessToken_then_uuid_found() {
    Mockito.when(accessToken.getSubject()).thenReturn(UUID.randomUUID().toString());

    Optional<UUID> optionalUUID = keycloakAuthenticationUtils.getUserUUIDFromRequest(request);
    assertTrue(optionalUUID.isPresent());
  }

  @Test
  void when_empty_uuid_in_accessToken_then_uuid_not_found() {
    Mockito.when(accessToken.getSubject()).thenReturn("");

    Optional<UUID> optionalUUID = keycloakAuthenticationUtils.getUserUUIDFromRequest(request);
    assertFalse(optionalUUID.isPresent());
  }

  @Test
  void when_blank_uuid_in_accessToken_then_uuid_not_found() {
    Mockito.when(accessToken.getSubject()).thenReturn("   \n   \t       ");

    Optional<UUID> optionalUUID = keycloakAuthenticationUtils.getUserUUIDFromRequest(request);
    assertFalse(optionalUUID.isPresent());
  }

  @Test
  void when_invalid_uuid_in_accessToken_then_uuid_not_found() {
    Mockito.when(accessToken.getSubject()).thenReturn("573a2d77-917544f3-8170-7b60923c53b7");

    Optional<UUID> optionalUUID = keycloakAuthenticationUtils.getUserUUIDFromRequest(request);
    assertFalse(optionalUUID.isPresent());
  }
}
