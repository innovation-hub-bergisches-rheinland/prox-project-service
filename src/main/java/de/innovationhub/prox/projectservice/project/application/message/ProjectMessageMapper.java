package de.innovationhub.prox.projectservice.project.application.message;

import de.innovationhub.prox.projectservice.project.application.message.request.CreateProjectRequest;
import de.innovationhub.prox.projectservice.project.application.message.request.UpdateProjectRequest;
import de.innovationhub.prox.projectservice.project.application.message.response.ReadProjectResponse;
import de.innovationhub.prox.projectservice.project.domain.Project;
import de.innovationhub.prox.projectservice.project.domain.ProjectContext;
import org.springframework.stereotype.Service;

@Service
public class ProjectMessageMapper {
  public Project toDomain(CreateProjectRequest createProjectRequest, ProjectContext context) {
    return Project.builder()
        .name(createProjectRequest.name())
        .description(createProjectRequest.description())
        .shortDescription(createProjectRequest.shortDescription())
        .requirements(createProjectRequest.requirements())
        .status(createProjectRequest.status())
        .context(context)
        .supervisorName(createProjectRequest.supervisorName())
        .build();
  }

  public ReadProjectResponse toResponse(Project project) {
    return new ReadProjectResponse(
        project.getProjectId().id(),
        project.getName(),
        project.getDescription(),
        project.getShortDescription(),
        project.getRequirements(),
        project.getStatus(),
        project.getSupervisorName()
    );
  }

  public Project updateProject(UpdateProjectRequest request, Project project) {
    project.setDescription(request.description());
    project.setName(request.name());
    project.setRequirements(request.requirement());
    project.setStatus(request.status());
    project.setSupervisorName(request.supervisorName());
    return project;
  }
}
