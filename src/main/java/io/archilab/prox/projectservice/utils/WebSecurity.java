package io.archilab.prox.projectservice.utils;

import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectRepository;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A security component which provides some useful security features and checks to secure the service
 */
@Component
public class WebSecurity {

  private final AuthenticationUtils authenticationUtils;

  @Autowired
  public WebSecurity(AuthenticationUtils authenticationUtils) {
    this.authenticationUtils = authenticationUtils;
  }

  /**
   * Check whether the requesting user equals the user who has created the project
   *
   * The users will be compared by obtaining the UUID from the request using the autowired {@link AuthenticationUtils}
   * and selecting the creator of the project
   * @param request Request which contains the requesting users principal
   * @param projectId Id of project on which the check should be performed
   * @param projectRepository Instance of projectRepository to obtain the associated project of <code>projectId</code>
   * @return <code>true</code> if requesting user and project creator are the same, otherwise <code>false</code>
   */
  public boolean checkProjectCreator(HttpServletRequest request, UUID projectId, ProjectRepository projectRepository) {
    Optional<UUID> optionalUUID = authenticationUtils.getUserUUIDFromRequest(request);
    Optional<Project> optionalProject = projectRepository.findById(projectId);
    return optionalProject.isPresent() && optionalUUID.isPresent() && optionalProject.get().getCreatorID().getCreatorID().equals(optionalUUID.get());
  }
}