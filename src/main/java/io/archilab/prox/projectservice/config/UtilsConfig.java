package io.archilab.prox.projectservice.config;

import io.archilab.prox.projectservice.utils.AuthenticationUtils;
import io.archilab.prox.projectservice.utils.KeycloakAuthenticationUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
  @Bean
  public AuthenticationUtils authenticationUtils() {
    return new KeycloakAuthenticationUtils();
  }
}