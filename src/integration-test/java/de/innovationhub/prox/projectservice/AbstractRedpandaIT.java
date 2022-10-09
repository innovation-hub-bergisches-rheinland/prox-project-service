package de.innovationhub.prox.projectservice;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ActiveProfiles("testcontainers-kafka")
public abstract class AbstractRedpandaIT {
  static RedpandaContainer REDPANDA_CONTAINER =
    new RedpandaContainer("docker.redpanda.com/vectorized/redpanda:v22.2.2");

  static {
    REDPANDA_CONTAINER.start();
  }

  @DynamicPropertySource
  static void setupKafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", REDPANDA_CONTAINER::getBootstrapServers);
  }
}
