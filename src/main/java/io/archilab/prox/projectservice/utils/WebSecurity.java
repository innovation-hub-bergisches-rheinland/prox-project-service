package io.archilab.prox.projectservice.utils;

import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectRepository;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSecurity {

  private final AuthenticationUtils authenticationUtils;

  @Autowired
  public WebSecurity(AuthenticationUtils authenticationUtils) {
    this.authenticationUtils = authenticationUtils;
  }

  public boolean checkProjectCreator(HttpServletRequest request, UUID projectId, ProjectRepository projectRepository) {
    Optional<UUID> optionalUUID = authenticationUtils.getUserUUIDFromRequest(request);
    Optional<Project> optionalProject = projectRepository.findById(projectId);
    return optionalProject.isPresent() && optionalUUID.isPresent() && optionalProject.get().getCreatorID().getCreatorID().equals(optionalUUID.get());
  }
}