package de.innovationhub.prox.projectservice.config;


import de.innovationhub.prox.projectservice.utils.AuthenticationUtils;
import de.innovationhub.prox.projectservice.utils.KeycloakAuthenticationUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
  @Bean
  public AuthenticationUtils authenticationUtils() {
    return new KeycloakAuthenticationUtils();
  }
}
