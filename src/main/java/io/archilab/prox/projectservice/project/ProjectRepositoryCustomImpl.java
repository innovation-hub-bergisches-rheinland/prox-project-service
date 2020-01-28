package io.archilab.prox.projectservice.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestParam;

public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

  @Autowired @Lazy private ProjectRepository projectRepository;

  @Override
  public List<Project> findAllByIds(@RequestParam("projectIds") UUID[] projectIds) {
    List<Project> specificProjects = new ArrayList<>();
    for (UUID projectId : projectIds) {
      Optional<Project> project = projectRepository.findById(projectId);
      if (project.isPresent()) {
        specificProjects.add(project.get());
      }
    }
    return specificProjects;
  }
}
