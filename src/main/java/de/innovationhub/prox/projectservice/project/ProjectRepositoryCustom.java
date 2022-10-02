package de.innovationhub.prox.projectservice.project;


import io.micrometer.core.lang.Nullable;
import java.util.Collection;
import java.util.List;

public interface ProjectRepositoryCustom {

  List<Project> filterProjects(
    @Nullable ProjectStatus status,
    @Nullable Collection<String> specializationKeys,
    @Nullable Collection<String> moduleTypeKeys,
    @Nullable String text);

  List<Project> search(String query);
}
