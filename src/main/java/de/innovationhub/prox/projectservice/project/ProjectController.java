package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectCollectionDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectDto;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ProjectController {
  private final ProjectRepository projectRepository;
  private final SpecializationRepository specializationRepository;
  private final ModuleTypeRepository moduleTypeRepository;
  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;

  @Autowired
  public ProjectController(ProjectRepository projectRepository,
      SpecializationRepository specializationRepository, ModuleTypeRepository moduleTypeRepository,
      UserRepository userRepository,
      OrganizationRepository organizationRepository) {
    this.projectRepository = projectRepository;
    this.specializationRepository = specializationRepository;
    this.moduleTypeRepository = moduleTypeRepository;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
  }

  /*
   * ----------------------------
   * CRUD
   * ----------------------------
   */

  @GetMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> getAllProjects() {
    var projects = this.projectRepository.findAll();
    return ResponseEntity.ok(ReadProjectCollectionDto.fromProjects(projects));
  }

  @GetMapping(value = "/projects/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> getProjectById(@PathVariable("id") UUID id) {
    return this.projectRepository.findById(id)
        .map(ReadProjectDto::fromProject)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @PutMapping(value = "/projects/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> updateProjectById(@PathVariable("id") UUID id, @RequestBody CreateProjectDto projectDto) {
    var project =  this.projectRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    // TODO: Proper mapping
    project.setName(projectDto.name());
    project.setDescription(projectDto.description());
    project.setShortDescription(projectDto.shortDescription());
    project.setRequirement(projectDto.requirement());
    project.setStatus(projectDto.status());
    project.setSupervisorName(project.getSupervisorName());

    var updated = projectRepository.save(project);
    return ResponseEntity.ok(ReadProjectDto.fromProject(updated));
  }

  @PutMapping(value = "/projects/{id}/specializations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> setProjectSpecializationsById(@PathVariable("id") UUID id, @RequestBody Set<String> specializationKeys) {
    var project =  this.projectRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    var specializations = this.specializationRepository.findAllByKeyIn(specializationKeys);
    project.setSpecializations(specializations);
    var updated = projectRepository.save(project);
    return ResponseEntity.ok(ReadProjectDto.fromProject(updated));
  }

  @PutMapping(value = "/projects/{id}/modules", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> setProjectModuleTypesById(@PathVariable("id") UUID id, @RequestBody Set<String> moduleTypeKeys) {
    var project =  this.projectRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    var moduleTypes = this.moduleTypeRepository.findAllByKeyIn(moduleTypeKeys);
    project.setModules(moduleTypes);
    var updated = projectRepository.save(project);
    return ResponseEntity.ok(ReadProjectDto.fromProject(updated));
  }

  @DeleteMapping(value = "/projects/{id}")
  public @ResponseBody ResponseEntity<Void> deleteProjectById(@PathVariable("id") UUID id) {
    var project =  this.projectRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    this.projectRepository.delete(project);
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
      @RequestParam(name = "text", required = false) String text,
      Sort sort
  ) {
    var projects = this.projectRepository.filterProjects(status, specializationKeys, moduleTypeKeys, text, sort);
    return ResponseEntity.ok(ReadProjectCollectionDto.fromProjects(projects));
  }

  /*
   * ----------------------------
   * Contexts
   * ----------------------------
   */

  @Transactional
  @PostMapping(value = "/user/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> createProjectForAuthenticatedUser(@RequestBody CreateProjectDto project, Authentication authentication) {
    var id = UUID.fromString(authentication.getName());
    return this.createUserProject(id, project);
  }

  @GetMapping(value = "/user/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> getProjectsOfAuthenticatedUser(Authentication authentication) {
    var id = UUID.fromString(authentication.getName());
    return getProjectsOfUser(id);
  }

  @Transactional
  @PostMapping(value = "/users/{id}/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> createUserProject(@PathVariable("id") UUID userId, @RequestBody CreateProjectDto project) {
    var user = this.userRepository.findById(userId)
        .orElse(userRepository.save(new User(userId)));
    var createdProject = createProject(project, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
  }

  @GetMapping(value = "/users/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> getProjectsOfUser(@PathVariable("id") UUID userId) {
    var projects = this.projectRepository.findByOwner(userId, User.DISCRIMINATOR);
    return ResponseEntity.ok(ReadProjectCollectionDto.fromProjects(projects));
  }

  @Transactional
  @PostMapping(value ="/organizations/{id}/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectDto> createOrganizationProject(@PathVariable("id") UUID orgId, @RequestBody CreateProjectDto project) {
    var org = this.organizationRepository.findById(orgId)
        .orElse(organizationRepository.save(new Organization(orgId)));
    var createdProject = createProject(project, org);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
  }

  @GetMapping(value = "/organizations/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> getProjectsOfOrganization(@PathVariable("id") UUID orgId) {
    var projects = this.projectRepository.findByOwner(orgId, Organization.DISCRIMINATOR);
    return ResponseEntity.ok(ReadProjectCollectionDto.fromProjects(projects));
  }

  private ReadProjectDto createProject(CreateProjectDto projectDto, AbstractOwner owner) {
    var project = projectDto.toProject();
    project.setOwner(owner);
    var savedProject = projectRepository.save(project);
    return ReadProjectDto.fromProject(savedProject);
  }
}
