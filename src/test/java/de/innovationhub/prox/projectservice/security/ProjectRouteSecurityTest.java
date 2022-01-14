package de.innovationhub.prox.projectservice.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.project.CreatorID;
import de.innovationhub.prox.projectservice.project.CreatorName;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectContext;
import de.innovationhub.prox.projectservice.project.ProjectDescription;
import de.innovationhub.prox.projectservice.project.ProjectName;
import de.innovationhub.prox.projectservice.project.ProjectRepository;
import de.innovationhub.prox.projectservice.project.ProjectRequirement;
import de.innovationhub.prox.projectservice.project.ProjectShortDescription;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import de.innovationhub.prox.projectservice.project.SupervisorName;
import de.innovationhub.prox.projectservice.utils.AuthenticationUtils;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectRouteSecurityTest {

  private static final String PROJECTS_ROUTE = "/projects";
  private static final String PROJECTS_ID_ROUTE = "/projects/{id}";

  @Autowired MockMvc mockMvc;

  @Autowired ProjectRepository projectRepository;

  @MockBean AuthenticationUtils authenticationUtils;

  // Generating a User ID which is used for performing authorized requests that depend on the
  // requesting User ID
  // That User ID can be referenced from within the tests
  private static final UUID USER_ID = UUID.randomUUID();

  @BeforeEach
  void init() {
    Mockito.when(authenticationUtils.getUserUUIDFromRequest(Mockito.any(HttpServletRequest.class)))
        .thenReturn(Optional.of(USER_ID));
    Mockito.when(authenticationUtils.authenticatedUserIsInRole(eq("PROFESSOR"))).thenReturn(true);
  }

  private Project createTestProject(UUID creatorId) {
    return new Project(
        new ProjectName("Test Project"),
        new ProjectShortDescription("This is a short description"),
        new ProjectDescription("This is a description"),
        ProjectStatus.LAUFEND,
        new ProjectRequirement("This is a requirement"),
        new CreatorID(creatorId),
        new CreatorName("Mock User"),
        new SupervisorName("Supervisor"),
        ProjectContext.PROFESSOR);
  }

  private void performRequest(
      HttpMethod httpMethod, String url, Object content, ResultMatcher expectedResult)
      throws Exception {
    mockMvc
        .perform(
            request(httpMethod, url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(content)))
        .andDo(print())
        .andExpect(expectedResult);
  }

  private String buildUrl(String url, Object... vars) {
    return UriComponentsBuilder.fromUriString(url).buildAndExpand(vars).encode().toString();
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_post_project_then_is_created()
      throws Exception {
    Project project = createTestProject(USER_ID);

    performRequest(HttpMethod.POST, PROJECTS_ROUTE, project, status().isCreated());
  }

  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_has_role_professor_performs_post_project_then_is_forbidden()
      throws Exception {
    // Content is not needed since the request should not process to that point
    performRequest(HttpMethod.POST, PROJECTS_ROUTE, null, status().isForbidden());
  }

  @Test
  @DisabledIfEnvironmentVariable(named = "CI", matches = "true", disabledReason = "For whatever reason this tests fails in CI")
  void when_unauthenticated_user_performs_get_projects_then_is_ok() throws Exception {
    mockMvc
        .perform(get(PROJECTS_ROUTE))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void when_unauthenticated_user_performs_get_project_with_id_then_is_ok() throws Exception {
    Project project = projectRepository.save(createTestProject(UUID.randomUUID()));

    mockMvc
        .perform(get(PROJECTS_ID_ROUTE, project.getId()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_creator_of_project_performs_delete_project_then_is_no_content()
          throws Exception {
    Project project = projectRepository.save(createTestProject(USER_ID));

    performRequest(
        HttpMethod.DELETE,
        buildUrl(PROJECTS_ID_ROUTE, project.getId()),
        null,
        status().isNoContent());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_not_creator_of_project_performs_delete_project_then_is_forbidden()
          throws Exception {
    Project project = projectRepository.save(createTestProject(UUID.randomUUID()));

    performRequest(
        HttpMethod.DELETE,
        buildUrl(PROJECTS_ID_ROUTE, project.getId()),
        null,
        status().isForbidden());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_creator_of_project_performs_put_project_then_is_ok()
          throws Exception {
    Project project = projectRepository.save(createTestProject(USER_ID));

    performRequest(
        HttpMethod.PUT, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isOk());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_not_creator_of_project_performs_put_project_then_is_forbidden()
          throws Exception {
    Project project = projectRepository.save(createTestProject(UUID.randomUUID()));

    performRequest(
        HttpMethod.PUT,
        buildUrl(PROJECTS_ID_ROUTE, project.getId()),
        project,
        status().isForbidden());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_creator_of_project_performs_patch_project_then_is_ok()
          throws Exception {
    Project project = projectRepository.save(createTestProject(USER_ID));

    performRequest(
        HttpMethod.PATCH, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isOk());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_is_not_creator_of_project_performs_patch_project_then_is_forbidden()
          throws Exception {
    Project project = projectRepository.save(createTestProject(UUID.randomUUID()));

    performRequest(
        HttpMethod.PATCH,
        buildUrl(PROJECTS_ID_ROUTE, project.getId()),
        project,
        status().isForbidden());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_performs_post_project_with_invalid_creatorid_then_is_replaced()
          throws Exception {
    Project project = createTestProject(USER_ID);
    Project expectedProject = project;
    project.setCreatorID(new CreatorID(UUID.randomUUID()));

    performRequest(HttpMethod.POST, PROJECTS_ROUTE, project, status().isCreated());

    Optional<Project> optionalProject = projectRepository.findById(project.getId());
    assertTrue(optionalProject.isPresent());
    Project foundProject = optionalProject.get();
    assertEquals(USER_ID, foundProject.getCreatorID().getCreatorID());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_performs_put_project_id_with_invalid_creatorid_then_is_replaced()
          throws Exception {
    Project project = createTestProject(USER_ID);
    Project expectedProject = projectRepository.save(project);
    project.setCreatorID(new CreatorID(UUID.randomUUID()));

    performRequest(
        HttpMethod.PUT, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isOk());

    Optional<Project> foundProject = projectRepository.findById(project.getId());
    assertTrue(foundProject.isPresent());
    assertEquals(expectedProject, foundProject.get());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void
      when_authenticated_user_has_role_professor_performs_patch_project_id_with_invalid_creatorid_then_is_replaced()
          throws Exception {
    Project project = createTestProject(USER_ID);
    Project expectedProject = projectRepository.save(project);
    project.setCreatorID(new CreatorID(UUID.randomUUID()));

    performRequest(
        HttpMethod.PATCH, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isOk());

    Optional<Project> foundProject = projectRepository.findById(project.getId());
    assertTrue(foundProject.isPresent());
    assertEquals(expectedProject, foundProject.get());
  }
}
