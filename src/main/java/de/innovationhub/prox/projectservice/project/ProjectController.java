package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.utils.AuthenticationUtils;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@RepositoryRestController
public class ProjectController {

  @Autowired ProjectRepository projectRepository;

  @Autowired AuthenticationUtils authenticationUtils;

  /* Start Workaround
    Workaround since Validation is not available in RepositoryRestController
    https://stackoverflow.com/a/44304198/4567795
   */
  @Autowired
  private LocalValidatorFactoryBean validator;

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(validator);
  }
  //End Workaround

  /**
   * Replace Spring Data Rest POST Mapping with custom POST Mapping which defaults the Project
   * CreatorID
   */
  @PostMapping(
      value = "/projects",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
  public @ResponseBody ResponseEntity<?> postProject(
      HttpServletRequest request,
      @Valid @RequestBody Project project,
      PersistentEntityResourceAssembler resourceAssembler) {
    Optional<UUID> requestUUID = authenticationUtils.getUserUUIDFromRequest(request);
    if (requestUUID.isEmpty()) {
      return new ResponseEntity<>(
          HttpStatus
              .UNAUTHORIZED); // No User ID - case should never occur, still implemented for safety
    }
    UUID uuid = requestUUID.get();
    project.setCreatorID(new CreatorID(uuid));

    if(authenticationUtils.authenticatedUserIsInRole("PROFESSOR")) {
      project.setContext(ProjectContext.PROFESSOR);
    } else if(authenticationUtils.authenticatedUserIsInRole("COMPANY-MANAGER")) {
      project.setContext(ProjectContext.COMPANY);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No matching role for project context found");
    }

    Project savedProject = projectRepository.save(project);
    return new ResponseEntity<>(resourceAssembler.toFullResource(savedProject), HttpStatus.CREATED);
  }

}
