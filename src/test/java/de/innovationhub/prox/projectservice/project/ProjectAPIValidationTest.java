package de.innovationhub.prox.projectservice.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataJpa
@Transactional
class ProjectAPIValidationTest {

  private static final String PROJECTS_ROUTE = "/projects";
  private static final String PROJECTS_ID_ROUTE = "/projects/{id}";

  @Autowired MockMvc mockMvc;

  @Autowired ProjectRepository projectRepository;

  Project validProject =
      new Project(
          "Test Project",
          "This is a\n short description",
          "This is a description",
          ProjectStatus.LAUFEND,
          "This is a requirement",
          UUID.randomUUID(),
          "Mock User",
          "Supervisor",
          ProjectContext.PROFESSOR);

  private Project emptyProject = new Project();

  private Project nullProject = new Project(null, null, null, null, null, null, null, null, null);

  private Project emptyValueProject =
      new Project(
          "     ",
          "  ",
          "    ",
          ProjectStatus.LAUFEND,
          "\n \t",
          UUID.randomUUID(),
          "",
          "   ",
          ProjectContext.PROFESSOR);

  private Project longValueProject =
      new Project(
          createLongString(255),
          createLongString(10000),
          createLongString(10000),
          ProjectStatus.LAUFEND,
          createLongString(10000),
          UUID.randomUUID(),
          createLongString(10000),
          createLongString(255),
          ProjectContext.PROFESSOR);

  String createLongString(int max) {
    return "a".repeat(Math.max(0, max + 1));
  }

  @Test
  void when_post_empty_body_then_is_bad_request() throws Exception {
    mockMvc
        .perform(post(PROJECTS_ROUTE).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_post_empty_project_then_is_bad_request() throws Exception {
    mockMvc
        .perform(
            post(PROJECTS_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_post_null_project_then_is_bad_request() throws Exception {
    mockMvc
        .perform(
            post(PROJECTS_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(nullProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_post_empty_value_project_then_is_bad_request() throws Exception {
    mockMvc
        .perform(
            post(PROJECTS_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyValueProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_post_long_value_project_then_is_bad_request() throws Exception {
    mockMvc
        .perform(
            post(PROJECTS_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(longValueProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_empty_body_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            put(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_empty_project_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            put(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_null_project_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            put(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(nullProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_empty_value_project_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            put(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyValueProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_put_long_value_project_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            put(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(longValueProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_empty_project_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            patch(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_null_project_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            patch(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(nullProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_empty_value_project_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            patch(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(emptyValueProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void when_patch_long_value_project_then_is_bad_request() throws Exception {
    Project savedProject = projectRepository.save(validProject);

    mockMvc
        .perform(
            patch(PROJECTS_ID_ROUTE, savedProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(longValueProject)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }
}
