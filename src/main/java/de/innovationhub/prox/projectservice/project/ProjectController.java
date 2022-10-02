package de.innovationhub.prox.projectservice.project;


import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectCollectionDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectDto;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {
  private final ProjectService projectService;

  @Autowired
  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  /*
   * ----------------------------
   * CRUD
   * ----------------------------
   */

  @GetMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> getAllProjects() {
    return ResponseEntity.ok(projectService.getAll());
  }

  @GetMapping(value = "/projects/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> getProjectById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(this.projectService.get(id));
  }

  @PutMapping(
      value = "/projects/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> updateProjectById(
      @PathVariable("id") UUID id, @RequestBody CreateProjectDto projectDto) {
    return ResponseEntity.ok(projectService.update(id, projectDto));
  }

  @PutMapping(
      value = "/projects/{id}/specializations",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> setProjectSpecializationsById(
      @PathVariable("id") UUID id, @RequestBody Set<String> specializationKeys) {
    return ResponseEntity.ok(projectService.setSpecializations(id, specializationKeys));
  }

  @PutMapping(
      value = "/projects/{id}/modules",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> setProjectModuleTypesById(
      @PathVariable("id") UUID id, @RequestBody Set<String> moduleTypeKeys) {
    return ResponseEntity.ok(projectService.setModuleTypes(id, moduleTypeKeys));
  }

  @DeleteMapping(value = "/projects/{id}")
  public @ResponseBody ResponseEntity<Void> deleteProjectById(@PathVariable("id") UUID id) {
    this.projectService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /*
   * ----------------------------
   * Search
   * ----------------------------
   */

  @GetMapping(value = "/projects/search/filter", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> filterProjects(
    @RequestParam(name = "status", required = false) ProjectStatus status,
    @RequestParam(name = "specializationKeys", required = false) String[] specializationKeys,
    @RequestParam(name = "moduleTypeKeys", required = false) String[] moduleTypeKeys,
    @RequestParam(name = "text", required = false) String text) {
    return ResponseEntity.ok(
      projectService.filter(status, specializationKeys, moduleTypeKeys, text));
  }

  /*
   * ----------------------------
   * Contexts
   * ----------------------------
   */

  @Transactional
  @PostMapping(
      value = "/user/projects",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> createProjectForAuthenticatedUser(
      @RequestBody CreateProjectDto project, Authentication authentication) {
    var id = UUID.fromString(authentication.getName());
    return this.createUserProject(id, project);
  }

  @GetMapping(value = "/user/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> getProjectsOfAuthenticatedUser(
      Authentication authentication) {
    var id = UUID.fromString(authentication.getName());
    return getProjectsOfUser(id);
  }

  @Transactional
  @PostMapping(
      value = "/users/{id}/projects",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> createUserProject(
      @PathVariable("id") UUID userId, @RequestBody CreateProjectDto project) {
    var createdProject = this.projectService.createForUser(userId, project);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
  }

  @GetMapping(value = "/users/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> getProjectsOfUser(
      @PathVariable("id") UUID userId) {
    var projects = this.projectService.findByUser(userId);
    return ResponseEntity.ok(projects);
  }

  @Transactional
  @PostMapping(
      value = "/organizations/{id}/projects",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> createOrganizationProject(
      @PathVariable("id") UUID orgId, @RequestBody CreateProjectDto project) {
    var createdProject = this.projectService.createForOrganization(orgId, project);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
  }

  @GetMapping(value = "/organizations/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> getProjectsOfOrganization(
      @PathVariable("id") UUID orgId) {
    var projects = this.projectService.findByOrg(orgId);
    return ResponseEntity.ok(projects);
  }
}
