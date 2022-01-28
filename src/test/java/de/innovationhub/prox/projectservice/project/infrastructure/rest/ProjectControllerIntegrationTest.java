package de.innovationhub.prox.projectservice.project.infrastructure.rest;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.project.domain.Project;
import de.innovationhub.prox.projectservice.project.domain.Project.ProjectId;
import de.innovationhub.prox.projectservice.project.domain.ProjectRepository;
import de.innovationhub.prox.projectservice.project.domain.ProjectStatus;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ProjectControllerIntegrationTest {
  @Autowired
  MockMvc mockMvc;

  @Autowired
  ProjectRepository projectRepository;

  @BeforeEach
  void setUp() {
    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  void shouldCreateProject() {
    var createdId = given()
        .contentType("application/json")
        .accept("application/json")
        .body("""
            {
              "name": "TP123",
              "description": "TP456",
              "shortDescription": "TP789",
              "requirements": "TP753",
              "supervisorName": "TP159",
              "status": "AVAILABLE"
            }
            """)
    .when()
        .post("/projects")
    .then()
        .status(HttpStatus.CREATED)
    .extract()
        .jsonPath().getUUID("id");

    assertThat(projectRepository.findProjectById(new ProjectId(createdId)))
        .isNotEmpty().get()
        .extracting("name", "description", "shortDescription", "requirements", "supervisorName", "status")
        .doesNotContainNull()
        .containsExactly("TP123", "TP456", "TP789", "TP753", "TP159", ProjectStatus.AVAILABLE);
  }
}
