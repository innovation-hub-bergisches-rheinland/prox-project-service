package de.innovationhub.prox.projectservice.project.infrastructure.persistence;

import de.innovationhub.prox.projectservice.project.domain.ContextId;
import de.innovationhub.prox.projectservice.project.domain.Project;
import de.innovationhub.prox.projectservice.project.domain.Project.ProjectId;
import de.innovationhub.prox.projectservice.project.domain.ProjectContext;
import org.springframework.stereotype.Service;

@Service
public class ProjectJpaMapper {
  public Project toDomain(ProjectJpaEntity jpa) {
    return Project.builder()
        .projectId(new ProjectId(jpa.getProjectId()))
        .context(this.toDomain(jpa.getContext()))
        .name(jpa.getName())
        .description(jpa.getDescription())
        .shortDescription(jpa.getShortDescription())
        .supervisorName(jpa.getSupervisorName())
        .requirements(jpa.getRequirement())
        .status(jpa.getStatus())
        .build();
  }

  public ProjectJpaEntity toPersistence(Project domain) {
    return ProjectJpaEntity.builder()
        .projectId(domain.getProjectId().id())
        .context(this.toPersistence(domain.getContext()))
        .name(domain.getName())
        .description(domain.getDescription())
        .shortDescription(domain.getShortDescription())
        .supervisorName(domain.getSupervisorName())
        .requirement(domain.getRequirements())
        .status(domain.getStatus())
        .build();
  }

  public ProjectContextJpa toPersistence(ProjectContext context) {
    return new ProjectContextJpa(context.getContextDefinition(), context.getId().id(), context.getContextName());
  }

  public ProjectContext toDomain(ProjectContextJpa contextJpa) {
    return new ProjectContext(contextJpa.getContext(),
        new ContextId(contextJpa.getContextId()),
        contextJpa.getContextName());
  }
}
