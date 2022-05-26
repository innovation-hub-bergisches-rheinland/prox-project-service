package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.projection.ProjectExcerpt;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;

  public ProjectController(ProjectRepository projectRepository, UserRepository userRepository,
      OrganizationRepository organizationRepository) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
  }

  @Transactional
  @PostMapping(value = "/user/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<?> createProjectForAuthenticatedUser(@RequestBody CreateProjectDto project, Authentication authentication) {
    var id = UUID.fromString(authentication.getName());
    return this.createUserProject(id, project);
  }

  @GetMapping(value = "/user/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<?> getProjectsOfAuthenticatedUser(Authentication authentication) {
    var id = UUID.fromString(authentication.getName());
    return getProjectsOfUser(id);
  }

  @Transactional
  @PostMapping(value = "/users/{id}/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<?> createUserProject(@PathVariable("id") UUID userId, @RequestBody CreateProjectDto project) {
    var user = this.userRepository.findById(userId)
        .orElse(userRepository.save(new User(userId)));
    var createdProject = createProject(project, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(createdProject));
  }

  @GetMapping(value = "/users/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<?> getProjectsOfUser(@PathVariable("id") UUID userId) {
    var projects = this.projectRepository.findByOwner(userId, User.DISCRIMINATOR);
    if(projects.isEmpty()) {
      var wrappers = new EmbeddedWrappers(false);
      var wrapper = wrappers.emptyCollectionOf(Project.class);
      return ResponseEntity.ok(CollectionModel.of(Arrays.asList(wrapper)));
    }
    return ResponseEntity.ok(CollectionModel.of(projects));
  }

  @Transactional
  @PostMapping(value ="/organizations/{id}/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<?> createOrganizationProject(@PathVariable("id") UUID orgId, @RequestBody CreateProjectDto project) {
    var org = this.organizationRepository.findById(orgId)
        .orElse(organizationRepository.save(new Organization(orgId)));
    var createdProject = createProject(project, org);
    return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(createdProject));
  }

  @GetMapping(value = "/organizations/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<?> getProjectsOfOrganization(@PathVariable("id") UUID orgId) {
    var projects = this.projectRepository.findByOwner(orgId, Organization.DISCRIMINATOR);
    if(projects.isEmpty()) {
      var wrappers = new EmbeddedWrappers(false);
      var wrapper = wrappers.emptyCollectionOf(Project.class);
      return ResponseEntity.ok(CollectionModel.of(Arrays.asList(wrapper)));
    }
    return ResponseEntity.ok(CollectionModel.of(projects.stream().map(EntityModel::of).collect(Collectors.toList())));
  }

  private Project createProject(CreateProjectDto projectDto, AbstractOwner owner) {
    var project = projectDto.toProject();
    project.setOwner(owner);
    return projectRepository.save(project);
  }
}
