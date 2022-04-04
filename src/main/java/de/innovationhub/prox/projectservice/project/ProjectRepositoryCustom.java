package de.innovationhub.prox.projectservice.project;


import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.core.annotation.RestResource;

public interface ProjectRepositoryCustom {

  @RestResource(exported = true, path = "findAvailableProjectsOfCreator")
  List<Project> findAvailableProjectsOfCreator(final UUID creatorId, Sort sort);

  @RestResource(exported = true, path = "findRunningProjectsOfCreator")
  List<Project> findRunningProjectsOfCreator(final UUID creatorId, Sort sort);

  @RestResource(exported = true, path = "findFinishedProjectsOfCreator")
  List<Project> findinishedProjectsOfCreator(final UUID creatorId, Sort sort);

  @RestResource(exported = true, path = "findRunningAndFinishedProjectsOfCreator")
  List<Project> findRunningAndFinishedProjectsOfCreator(final UUID creatorId, Sort sort);

  @RestResource(exported = true, path = "filterProjects")
  List<Project> filterProjects(
      ProjectStatus status,
      String[] specializationKeys,
      String[] moduleTypeKeys,
      String text,
      Sort sort);

  @RestResource(exported = true, path = "findProjectStatsOfCreator")
  ProjectStats findProjectStatsOfCreator(final UUID creatorId, Sort sort);
}
