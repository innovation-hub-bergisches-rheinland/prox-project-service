package de.innovationhub.prox.projectservice.project.infrastructure.rest;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.project.application.ProjectApplicationService;
import de.innovationhub.prox.projectservice.project.application.message.request.CreateProjectRequest;
import de.innovationhub.prox.projectservice.project.application.message.request.UpdateProjectRequest;
import de.innovationhub.prox.projectservice.project.application.message.response.ReadProjectResponse;
import de.innovationhub.prox.projectservice.project.domain.ProjectContext;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {
  @MockBean
  ProjectApplicationService projectService;

  @Autowired
  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  void getAll() {
    // Given
    PodamFactory factory = new PodamFactoryImpl();
    List<ReadProjectResponse> projects = factory.manufacturePojo(ArrayList.class, ReadProjectResponse.class);
    when(projectService.findAll()).thenReturn(projects);

    // When
    var list = given()
        .auth().none()
        .accept("application/json")
    .when()
        .get("/projects")
    .then()
        .status(HttpStatus.OK)
    .extract()
        .jsonPath().getList(".", ReadProjectResponse.class);

    // Then
    assertThat(list).containsExactlyInAnyOrderElementsOf(projects);
    verify(projectService).findAll();
  }

  @Test
  void postProject() {
    // Given
    PodamFactory factory = new PodamFactoryImpl();
    CreateProjectRequest request = factory.manufacturePojo(CreateProjectRequest.class);
    ReadProjectResponse response = factory.manufacturePojo(ReadProjectResponse.class);
    when(projectService.save(eq(request), any())).thenReturn(response);

    // When
    var res = given()
        .auth().none()
        .accept("application/json")
        .contentType("application/json")
        .body(request)
    .when()
        .post("/projects")
    .then()
        .status(HttpStatus.CREATED)
        .extract()
        .jsonPath().getObject(".", ReadProjectResponse.class);

    // Then
    assertThat(res).isEqualTo(response);
    verify(projectService).save(eq(request), any());
  }

  @Test
  void getProjectById() {
    // Given
    PodamFactory factory = new PodamFactoryImpl();
    ReadProjectResponse response = factory.manufacturePojo(ReadProjectResponse.class);
    UUID id = UUID.randomUUID();
    when(projectService.findById(eq(id))).thenReturn(Optional.of(response));

    // When
    var res = given()
        .auth().none()
        .accept("application/json")
    .when()
        .get("/projects/{id}", id.toString())
    .then()
        .status(HttpStatus.OK)
        .extract()
        .jsonPath().getObject(".", ReadProjectResponse.class);

    // Then
    assertThat(res).isEqualTo(response);
    verify(projectService).findById(eq(id));
  }

  @Test
  void getProjectByIdShouldReturn404() {
    // Given
    UUID id = UUID.randomUUID();
    when(projectService.findById(eq(id))).thenReturn(Optional.empty());

    // When
    given()
        .auth().none()
    .accept("application/json")
        .when()
        .get("/projects/{id}", id.toString())
    .then()
        .status(HttpStatus.NOT_FOUND);

    // Then
    verify(projectService).findById(eq(id));
  }

  @Test
  void updateProjectWithId() {
    // Given
    PodamFactory factory = new PodamFactoryImpl();
    UpdateProjectRequest request = factory.manufacturePojo(UpdateProjectRequest.class);
    UUID id = UUID.randomUUID();
    ReadProjectResponse response = factory.manufacturePojo(ReadProjectResponse.class);
    when(projectService.update(eq(id), eq(request))).thenReturn(response);

    // When
    var res = given()
        .auth().none()
        .accept("application/json")
        .contentType("application/json")
        .body(request)
    .when()
        .put("/projects/{id}", id.toString())
    .then()
        .status(HttpStatus.OK)
        .extract()
        .jsonPath().getObject(".", ReadProjectResponse.class);

    // Then
    assertThat(res).isEqualTo(response);
    verify(projectService).update(eq(id), eq(request));
  }

  @Test
  void deleteProjectById() {
    // Given
    UUID id = UUID.randomUUID();
    // When
    given()
        .auth().none()
    .when()
        .delete("/projects/{id}", id.toString())
    .then()
        .status(HttpStatus.NO_CONTENT);

    // Then
    verify(projectService).delete(eq(id));
  }
}