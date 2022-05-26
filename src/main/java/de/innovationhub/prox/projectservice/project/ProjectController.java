package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
  public @ResponseBody ResponseEntity<?> createProjectForAuthenticatedUser(@RequestBody Project project, Authentication authentication) {
    var id = UUID.fromString(authentication.getName());
    return this.createUserProject(id, project);
  }

  @Transactional
  @PostMapping(value = "/users/{id}/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<?> createUserProject(@PathVariable("id") UUID userId, @RequestBody Project project) {
    var user = this.userRepository.findById(userId)
        .orElse(userRepository.save(new User(userId)));
    var createdProject = createProject(project, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(createdProject));
  }

  @Transactional
  @PostMapping(value ="/organizations/{id}/projects", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<?> createOrganizationProject(@PathVariable("id") UUID orgId, @RequestBody Project project) {
    var org = this.organizationRepository.findById(orgId)
        .orElse(organizationRepository.save(new Organization(orgId)));
    var createdProject = createProject(project, org);
    return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(createdProject));
  }

  private Project createProject(Project project, AbstractOwner owner) {
    project.setOwner(owner);
    return projectRepository.save(project);
  }
}
