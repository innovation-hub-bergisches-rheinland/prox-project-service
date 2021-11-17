package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.module.ModuleType;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProjectRepositoryCustom {

  List<Project> findAllByIds(@RequestParam("projectIds") UUID[] projectIds);

  @RestResource(exported = true, path = "findAvailableProjectsOfCreator")
  Set<Project> findAvailableProjectsOfCreator(final UUID creatorId);

  @RestResource(exported = true, path = "findRunningProjectsOfCreator")
  Set<Project> findRunningProjectsOfCreator(final UUID creatorId);

  @RestResource(exported = true, path = "findFinishedProjectsOfCreator")
  Set<Project> findinishedProjectsOfCreator(final UUID creatorId);

  @RestResource(exported = true, path = "findRunningAndFinishedProjectsOfCreator")
  Set<Project> findRunningAndFinishedProjectsOfCreator(final UUID creatorId);

  @RestResource(exported = true, path = "filterProjects")
  Set<Project> filterProjects(ProjectStatus status, String[] moduleTypeKeys, String text);

  @RestResource(exported = true, path = "findProjectStatsOfCreator")
  ProjectStats findProjectStatsOfCreator(final UUID creatorId);
}
