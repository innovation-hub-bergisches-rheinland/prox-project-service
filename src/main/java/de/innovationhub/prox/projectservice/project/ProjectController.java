package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.utils.AuthenticationUtils;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class ProjectController {

  @Autowired ProjectRepository projectRepository;

  @Autowired AuthenticationUtils authenticationUtils;

  /**
   * Replace Spring Data Rest POST Mapping with custom POST Mapping which defaults the Project
   * CreatorID
   */
  @PostMapping(
      value = "/projects",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
  public @ResponseBody ResponseEntity<?> postProject(
      HttpServletRequest request,
      @RequestBody Project project,
      PersistentEntityResourceAssembler resourceAssembler) {
    Optional<UUID> requestUUID = authenticationUtils.getUserUUIDFromRequest(request);
    if (requestUUID.isEmpty()) {
      return new ResponseEntity<>(
          HttpStatus
              .UNAUTHORIZED); // No User ID - case should never occur, still implemented for safety
    }
    UUID uuid = requestUUID.get();
    project.setCreatorID(new CreatorID(uuid));
    Project savedProject = projectRepository.save(project);
    return new ResponseEntity<>(resourceAssembler.toFullResource(savedProject), HttpStatus.CREATED);
  }
}