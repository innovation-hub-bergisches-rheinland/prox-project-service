package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.keycloak.WithMockKeycloakAuth;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomAuditorAwareTest {

  CustomAuditorAware customAuditorAware = new CustomAuditorAware();

  @Test
  @WithMockKeycloakAuth(claims = @OpenIdClaims(sub = "4f04c5da-1117-475a-a26a-7b65301ebd36"))
  void shouldGetUUIDFromTokenSubject() {
    assertThat(customAuditorAware.getCurrentAuditor())
        .isNotEmpty()
        .get()
        .isEqualTo(UUID.fromString("4f04c5da-1117-475a-a26a-7b65301ebd36"));
  }

  @Test
  @WithMockKeycloakAuth(claims = @OpenIdClaims(sub = "test@example.com"))
  void shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> customAuditorAware.getCurrentAuditor());
  }

  @Test
  void shouldBeEmpty() {
    assertThat(customAuditorAware.getCurrentAuditor()).isEmpty();
  }
}
