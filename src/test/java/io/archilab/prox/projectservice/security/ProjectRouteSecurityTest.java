/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.archilab.prox.projectservice.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.archilab.prox.projectservice.project.CreatorID;
import io.archilab.prox.projectservice.project.CreatorName;
import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectDescription;
import io.archilab.prox.projectservice.project.ProjectName;
import io.archilab.prox.projectservice.project.ProjectRepository;
import io.archilab.prox.projectservice.project.ProjectRequirement;
import io.archilab.prox.projectservice.project.ProjectShortDescription;
import io.archilab.prox.projectservice.project.ProjectStatus;
import io.archilab.prox.projectservice.project.SupervisorName;
import io.archilab.prox.projectservice.utils.AuthenticationUtils;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ProjectRepository projectRepository;

  @MockBean
  AuthenticationUtils authenticationUtils;

  //Generating a User ID which is used for performing authorized requests that depend on the requesting User ID
  //That User ID can be referenced from within the tests
  private static final UUID USER_ID = UUID.randomUUID();

  @BeforeEach
  void init() {
    MockitoAnnotations.initMocks(ProjectRouteSecurityTest.class);
    Mockito.when(authenticationUtils.getUserUUIDFromRequest(Mockito.any(HttpServletRequest.class))).thenReturn(Optional.of(USER_ID));
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
        new SupervisorName("Supervisor")
    );
  }

  private void performRequest(HttpMethod httpMethod, String url, Object content, ResultMatcher expectedResult)
      throws Exception {
    mockMvc.perform(request(httpMethod, url)
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
  void when_authenticated_user_has_role_professor_performs_post_project_then_is_created() throws Exception {
    Project project = createTestProject(USER_ID);

    performRequest(HttpMethod.POST, PROJECTS_ROUTE, project, status().isCreated());
  }

  @WithMockUser(roles = {})
  @Test
  void when_authenticated_user_does_not_has_role_professor_performs_post_project_then_is_forbidden() throws Exception {
    //Content is not needed since the request should not process to that point
    performRequest(HttpMethod.POST, PROJECTS_ROUTE, null, status().isForbidden());
  }

  @Test
  void when_unauthenticated_user_performs_get_projects_then_is_ok() throws Exception {
    performRequest(HttpMethod.GET, PROJECTS_ROUTE, null, status().isOk());
  }

  @Test
  void when_unauthenticated_user_performs_get_project_with_id_then_is_ok() throws Exception {
    Project project = projectRepository.save(createTestProject(UUID.randomUUID()));

    mockMvc.perform(get(PROJECTS_ID_ROUTE, project.getId())).andDo(print()).andExpect(status().isOk());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_is_creator_of_project_performs_delete_project_then_is_no_content() throws Exception {
    Project project = projectRepository.save(createTestProject(USER_ID));

    performRequest(HttpMethod.DELETE, buildUrl(PROJECTS_ID_ROUTE, project.getId()), null, status().isNoContent());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_is_not_creator_of_project_performs_delete_project_then_is_forbidden() throws Exception {
    Project project = projectRepository.save(createTestProject(UUID.randomUUID()));

    performRequest(HttpMethod.DELETE, buildUrl(PROJECTS_ID_ROUTE, project.getId()), null, status().isForbidden());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_is_creator_of_project_performs_put_project_then_is_ok() throws Exception {
    Project project = projectRepository.save(createTestProject(USER_ID));

    performRequest(HttpMethod.PUT, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isOk());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_is_not_creator_of_project_performs_put_project_then_is_forbidden() throws Exception {
    Project project = projectRepository.save(createTestProject(UUID.randomUUID()));

    performRequest(HttpMethod.PUT, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isForbidden());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_is_creator_of_project_performs_patch_project_then_is_ok() throws Exception {
    Project project = projectRepository.save(createTestProject(USER_ID));

    performRequest(HttpMethod.PATCH, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isOk());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_is_not_creator_of_project_performs_patch_project_then_is_forbidden() throws Exception {
    Project project = projectRepository.save(createTestProject(UUID.randomUUID()));

    performRequest(HttpMethod.PATCH, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isForbidden());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_post_project_with_invalid_creatorid_then_is_replaced() throws Exception {
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
  void when_authenticated_user_has_role_professor_performs_put_project_id_with_invalid_creatorid_then_is_replaced() throws Exception {
    Project project = createTestProject(USER_ID);
    Project expectedProject = projectRepository.save(project);
    project.setCreatorID(new CreatorID(UUID.randomUUID()));

    performRequest(HttpMethod.PUT, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isOk());

    Optional<Project> foundProject = projectRepository.findById(project.getId());
    assertTrue(foundProject.isPresent());
    assertEquals(expectedProject, foundProject.get());
  }

  @WithMockUser(roles = {"professor"})
  @Test
  void when_authenticated_user_has_role_professor_performs_patch_project_id_with_invalid_creatorid_then_is_replaced() throws Exception {
    Project project = createTestProject(USER_ID);
    Project expectedProject = projectRepository.save(project);
    project.setCreatorID(new CreatorID(UUID.randomUUID()));

    performRequest(HttpMethod.PATCH, buildUrl(PROJECTS_ID_ROUTE, project.getId()), project, status().isOk());

    Optional<Project> foundProject = projectRepository.findById(project.getId());
    assertTrue(foundProject.isPresent());
    assertEquals(expectedProject, foundProject.get());
  }

}
