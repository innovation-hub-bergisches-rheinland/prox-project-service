package de.innovationhub.prox.projectservice.project;


import de.innovationhub.prox.projectservice.project.dto.ReadProjectCollectionDto;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectAdminController {
  private final ProjectService projectService;

  @Autowired
  public ProjectAdminController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @PostMapping(value = "/projects/reconciliation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProjectCollectionDto> reconcile(
    @RequestBody List<UUID> ids) {
    return ResponseEntity.ok(this.projectService.reconcile(ids));
  }
}
