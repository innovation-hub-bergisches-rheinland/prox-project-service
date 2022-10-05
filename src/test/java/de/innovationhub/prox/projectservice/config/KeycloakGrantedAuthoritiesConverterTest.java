package de.innovationhub.prox.projectservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class KeycloakGrantedAuthoritiesConverterTest {
  KeycloakGrantedAuthoritiesConverter converter = new KeycloakGrantedAuthoritiesConverter();

  @Test
  void shouldExtractFeatures() {
    var features = List.of("ALL", "proposals");
    Map<String, Object> featureClaim = Map.of("features", features);
    var jwt = createJwt(featureClaim);

    var authorities = converter.convert(jwt);

    assertThat(authorities)
      .hasSize(2)
      .map(GrantedAuthority::getAuthority)
      .contains("FEATURE_ALL", "FEATURE_proposals");
  }

  @Test
  void shouldExtractRealmRoles() {
    var roles = List.of("professor", "student", "admin");
    Map<String, Object> roleClaim = Map.of("roles", roles);
    Map<String, Object> realmAccessClaim = Map.of("realm_access", roleClaim);
    var jwt = createJwt(realmAccessClaim);

    var authorities = converter.convert(jwt);

    assertThat(authorities)
      .hasSize(3)
      .map(GrantedAuthority::getAuthority)
      .contains("ROLE_professor", "ROLE_student", "ROLE_admin");
  }

  Jwt createJwt(Map<String, Object> claims) {
    // It's difficult to create a jwt here. So we're going to mock it.
    // Unfortunately we cannot use a spy because there is no public 0-arg constructor.
    // Therefore we delegate it to the mock creation.
    var mock = Mockito.mock(Jwt.class, CALLS_REAL_METHODS);

    // I think most of the methods should call the getClaims method.
    // This should work for most of the cases.
    when(mock.getClaims()).thenReturn(claims);

    return mock;
  }
}
