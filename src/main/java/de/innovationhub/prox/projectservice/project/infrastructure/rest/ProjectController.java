package de.innovationhub.prox.projectservice.project.infrastructure.rest;

import de.innovationhub.prox.projectservice.project.application.ProjectApplicationService;
import de.innovationhub.prox.projectservice.project.application.message.request.CreateProjectRequest;
import de.innovationhub.prox.projectservice.project.application.message.request.UpdateProjectRequest;
import de.innovationhub.prox.projectservice.project.application.message.response.ReadProjectResponse;
import de.innovationhub.prox.projectservice.project.domain.ContextDefinition;
import de.innovationhub.prox.projectservice.project.domain.ContextId;
import de.innovationhub.prox.projectservice.project.domain.ProjectContext;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("projects")
public class ProjectController {
  private final ProjectApplicationService projectService;

  @Autowired
  public ProjectController(
      ProjectApplicationService projectService) {
    this.projectService = projectService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ReadProjectResponse>> getAll() {
    return ResponseEntity.ok(this.projectService.findAll());
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReadProjectResponse> postProject(
      @RequestBody CreateProjectRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(this.projectService.save(request, new ProjectContext(
            ContextDefinition.PROFESSOR,
            new ContextId(UUID.randomUUID())
        ))); //TODO
  }

  @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReadProjectResponse> getProjectById(
      @PathVariable("id") UUID id
  ) {
    return this.projectService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReadProjectResponse> updateProjectWithId(
      @PathVariable("id") UUID id,
      @RequestBody UpdateProjectRequest request
  ) {
    var response = this.projectService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteProjectById(
      @PathVariable("id") UUID id
  ) {
    this.projectService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
