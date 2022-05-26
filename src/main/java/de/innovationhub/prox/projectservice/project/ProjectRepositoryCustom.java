package de.innovationhub.prox.projectservice.project;


import io.micrometer.core.lang.Nullable;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface ProjectRepositoryCustom {
  List<Project> filterProjects(
      @Nullable ProjectStatus status,
      @Nullable String[] specializationKeys,
      @Nullable String[] moduleTypeKeys,
      @Nullable String text,
      Sort sort);
}
