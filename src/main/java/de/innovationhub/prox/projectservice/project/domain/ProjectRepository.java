package de.innovationhub.prox.projectservice.project.domain;

import de.innovationhub.prox.projectservice.project.domain.Project.ProjectId;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
  List<Project> findAll();
  Optional<Project> findProjectById(ProjectId id);
  Project save(Project project);
  void delete(ProjectId projectId);
}
