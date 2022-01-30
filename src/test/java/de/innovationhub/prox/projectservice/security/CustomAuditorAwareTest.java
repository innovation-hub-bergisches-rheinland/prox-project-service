package de.innovationhub.prox.projectservice.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.keycloak.WithMockKeycloakAuth;
import de.innovationhub.prox.projectservice.config.JpaConfig;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;

@SpringBootTest
class CustomAuditorAwareTest {
  CustomAuditorAware customAuditorAware = new CustomAuditorAware();

  @Test
  @WithMockKeycloakAuth(claims = @OpenIdClaims(
      sub = "4f04c5da-1117-475a-a26a-7b65301ebd36"
  ))
  void shouldGetUUIDFromTokenSubject() {
    assertThat(customAuditorAware.getCurrentAuditor())
        .isNotEmpty().get()
        .isEqualTo(UUID.fromString("4f04c5da-1117-475a-a26a-7b65301ebd36"));
  }

  @Test
  @WithMockKeycloakAuth(claims = @OpenIdClaims(
      sub = "test@example.com"
  ))
  void shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> customAuditorAware.getCurrentAuditor());
  }

  @Test
  void shouldBeEmpty() {
    assertThat(customAuditorAware.getCurrentAuditor())
        .isEmpty();
  }
}