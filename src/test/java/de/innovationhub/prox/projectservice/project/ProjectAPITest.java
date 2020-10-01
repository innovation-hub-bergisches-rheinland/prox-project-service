package de.innovationhub.prox.projectservice.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.utils.AuthenticationUtils;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * These tests will test the controller mapping on /projects for basic CRUD functionality. As the
 * controllers are generated by Spring Data and an intense testing of them would end up as testing
 * the framework, the tests in this class should be considered as an integration test between API
 * and database.
 *
 * <p>The test class excludes the Security Filter Chain and mocks the web service Also it makes use
 * of the DataJpa test to rollback transactions after each test
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
class ProjectAPITest {
  private static final String PROJECTS_ROUTE = "/projects";
  private static final String PROJECTS_ID_ROUTE = "/projects/{id}";

  @Autowired MockMvc mockMvc;

  @Autowired ProjectRepository projectRepository;

  @MockBean AuthenticationUtils authenticationUtils;

  // Generating a User ID which is used for performing authorized requests that depend on the
  // requesting User ID
  // That User ID can be referenced from within the tests
  private static final UUID USER_ID = UUID.randomUUID();

  Project sampleProject =
      new Project(
          new ProjectName("Test Project"),
          new ProjectShortDescription("This is a short description"),
          new ProjectDescription("This is a description"),
          ProjectStatus.LAUFEND,
          new ProjectRequirement("This is a requirement"),
          new CreatorID(UUID.randomUUID()),
          new CreatorName("Mock User"),
          new SupervisorName("Supervisor"));

  // GET /projects/{id}
  @Test
  void when_get_to_projects_id_then_is_ok() throws Exception {
    projectRepository.save(sampleProject);

    mockMvc
        .perform(get(PROJECTS_ID_ROUTE, sampleProject.getId()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  // GET /projects
  @Test
  void when_get_to_projects_then_is_ok() throws Exception {
    projectRepository.save(sampleProject);

    mockMvc.perform(get(PROJECTS_ROUTE)).andDo(print()).andExpect(status().isOk());
  }

  // POST /projects
  @Test
  void when_valid_post_to_projects_then_project_is_saved() throws Exception {

    /*
     Since the default Spring Data REST POST Mapping is replaced and relies on AuthenticationUtils
     it needs to be mocked to obtain a valid UserID while setting CreatorID
    */
    Mockito.when(authenticationUtils.getUserUUIDFromRequest(Mockito.any(HttpServletRequest.class)))
        .thenReturn(Optional.of(USER_ID));
    sampleProject.setCreatorID(new CreatorID(USER_ID));

    mockMvc
        .perform(
            post(PROJECTS_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(sampleProject)))
        .andDo(print())
        .andExpect(status().isCreated());

    Optional<Project> foundProject = projectRepository.findById(sampleProject.getId());

    assertTrue(foundProject.isPresent());
    assertEquals(sampleProject, foundProject.get());
  }

  // DELETE /projects/{id}
  @Test
  void when_delete_to_projects_id_then_project_is_deleted() throws Exception {
    projectRepository.save(sampleProject);

    mockMvc
        .perform(delete(PROJECTS_ID_ROUTE, sampleProject.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());

    Optional<Project> foundProject = projectRepository.findById(sampleProject.getId());

    assertFalse(foundProject.isPresent());
  }

  // PUT /projects/{id}
  @Test
  void when_put_to_projects_id_then_project_is_updated() throws Exception {
    projectRepository.save(sampleProject);

    Project copiedProject = sampleProject;
    ProjectDescription updatedDescription = new ProjectDescription("Updated");
    copiedProject.setDescription(updatedDescription);

    mockMvc
        .perform(
            put(PROJECTS_ID_ROUTE, copiedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(copiedProject)))
        .andDo(print())
        .andExpect(status().isNoContent());

    Optional<Project> foundProject = projectRepository.findById(copiedProject.getId());

    assertTrue(foundProject.isPresent());
    assertEquals(copiedProject, foundProject.get());
  }

  // PUT /projects/{id}
  @Test
  void when_put_to_projects_id_then_project_is_created() throws Exception {
    mockMvc
        .perform(
            put(PROJECTS_ID_ROUTE, sampleProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(sampleProject)))
        .andDo(print())
        .andExpect(status().isCreated());

    Optional<Project> foundProject = projectRepository.findById(sampleProject.getId());

    assertTrue(foundProject.isPresent());
    assertEquals(sampleProject, foundProject.get());
  }

  // PATCH /projects/{id}
  @Test
  void when_patch_to_projects_id_then_project_is_updated() throws Exception {
    projectRepository.save(sampleProject);

    Project copiedProject = sampleProject;
    ProjectDescription updatedDescription = new ProjectDescription("Updated");
    copiedProject.setDescription(updatedDescription);

    mockMvc
        .perform(
            patch(PROJECTS_ID_ROUTE, sampleProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedDescription)))
        .andDo(print())
        .andExpect(status().isNoContent());

    Optional<Project> foundProject = projectRepository.findById(sampleProject.getId());

    assertTrue(foundProject.isPresent());
    assertEquals(copiedProject, foundProject.get());
  }
}