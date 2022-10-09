package de.innovationhub.prox.projectservice;

import javax.transaction.Transactional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

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
