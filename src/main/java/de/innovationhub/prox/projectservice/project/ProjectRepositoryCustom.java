package de.innovationhub.prox.projectservice.project;


import io.micrometer.core.lang.Nullable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.annotation.RestResource;

public interface ProjectRepositoryCustom {

  @RestResource(exported = true, path = "filterProjects")
  List<Project> filterProjects(
      @Nullable ProjectStatus status,
      @Nullable String[] specializationKeys,
      @Nullable String[] moduleTypeKeys,
      @Nullable String text,
      Sort sort);
}
