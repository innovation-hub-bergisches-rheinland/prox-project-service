package de.innovationhub.prox.projectservice.project;


import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.annotation.RestResource;

public interface ProjectRepositoryCustom {

  @RestResource(exported = true, path = "filterProjects")
  List<Project> filterProjects(
      ProjectStatus status,
      String[] specializationKeys,
      String[] moduleTypeKeys,
      String text,
      Sort sort);
}
