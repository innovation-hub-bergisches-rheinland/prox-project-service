package de.innovationhub.prox.projectservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;

@Configuration
public class KafkaConfig {
  @Bean
  public CommonLoggingErrorHandler errorHandler() {
    return new CommonLoggingErrorHandler();
  }
}
