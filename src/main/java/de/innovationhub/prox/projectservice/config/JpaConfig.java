package de.innovationhub.prox.projectservice.config;


import de.innovationhub.prox.projectservice.security.CustomAuditorAware;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

  @Bean
  public AuditorAware<UUID> auditorProvider() {
    return new CustomAuditorAware();
  }
}
