package de.innovationhub.prox.projectservice;

import javax.transaction.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("testcontainers-postgres")
@Transactional
public abstract class AbstractDatabaseIT {

  private static final String PSQL_DB = "project-db";
  private static final String PSQL_USERNAME = "project-service";
  private static final String PSQL_PASSWORD = "project-service";
  static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.1")
    .withDatabaseName(PSQL_DB)
    .withUsername(PSQL_USERNAME)
    .withPassword(PSQL_PASSWORD)
    .withReuse(true);

  static {
    postgreSQLContainer.start();
  }

  @DynamicPropertySource
  static void setupDB(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }
}
