package de.innovationhub.prox.projectservice.project;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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

  @Override
  public Set<Project> findAvailableProjectsOfCreator(UUID creatorId) {
    return projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(
        creatorId, ProjectStatus.VERFÜGBAR);
  }

  @Override
  public Set<Project> findRunningAndFinishedProjectsOfCreator(UUID creatorId) {
    return projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(
        creatorId, ProjectStatus.ABGESCHLOSSEN, ProjectStatus.LAUFEND);
  }

  @Override
  public Set<Project> filterProjects(ProjectStatus status, String[] moduleTypeKeys, String text) {
    // TODO refactor and use fuzzy search or something similar which does not require hardcoding
    return StreamSupport.stream(this.projectRepository.findAll().spliterator(), false)
        .filter(p -> status != null ? p.getStatus() == status : true)
        .filter(
            p ->
                moduleTypeKeys != null && moduleTypeKeys.length > 0
                    ? StreamSupport.stream(p.getModules().spliterator(), false)
                        .anyMatch(
                            m ->
                                Arrays.stream(moduleTypeKeys)
                                    .anyMatch(k -> k.equalsIgnoreCase(m.getKey())))
                    : true)
        .filter(
            p -> {
              if (text == null || text.length() <= 0) {
                return true;
              }
              var match = false;
              if (p.getCreatorName() != null && p.getCreatorName().getCreatorName() != null) {
                match |=
                    p.getCreatorName().getCreatorName().toLowerCase().contains(text.toLowerCase());
              }
              if (match == false
                  && p.getDescription() != null
                  && p.getDescription().getDescription() != null) {
                match |=
                    p.getDescription().getDescription().toLowerCase().contains(text.toLowerCase());
              }
              if (match == false
                  && p.getShortDescription() != null
                  && p.getShortDescription().getShortDescription() != null) {
                match |=
                    p.getShortDescription()
                        .getShortDescription()
                        .toLowerCase()
                        .contains(text.toLowerCase());
              }
              if (match == false && p.getName() != null && p.getName().getName() != null) {
                match |= p.getName().getName().toLowerCase().contains(text.toLowerCase());
              }
              if (match == false
                  && p.getRequirement() != null
                  && p.getRequirement().getRequirement() != null) {
                match |=
                    p.getRequirement().getRequirement().toLowerCase().contains(text.toLowerCase());
              }
              if (match == false
                  && p.getSupervisorName() != null
                  && p.getSupervisorName().getSupervisorName() != null) {
                match |=
                    p.getSupervisorName()
                        .getSupervisorName()
                        .toLowerCase()
                        .contains(text.toLowerCase());
              }
              return match;
            })
        .collect(Collectors.toSet());
  }

  @Override
  public ProjectStats findProjectStatsOfCreator(UUID creatorId) {
    var projects =
        this.projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(
            creatorId, ProjectStatus.LAUFEND, ProjectStatus.VERFÜGBAR, ProjectStatus.ABGESCHLOSSEN);
    return new ProjectStats(
        filterByStatusAndCount(projects, ProjectStatus.LAUFEND),
        filterByStatusAndCount(projects, ProjectStatus.ABGESCHLOSSEN),
        filterByStatusAndCount(projects, ProjectStatus.VERFÜGBAR));
  }

  private int filterByStatusAndCount(Set<Project> projects, ProjectStatus projectStatus) {
    return Math.toIntExact(projects.stream().filter(p -> p.getStatus() == projectStatus).count());
  }

  @Override
  public Set<Project> findRunningProjectsOfCreator(UUID creatorId) {
    return projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(
        creatorId, ProjectStatus.LAUFEND);
  }

  @Override
  public Set<Project> findinishedProjectsOfCreator(UUID creatorId) {
    return projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(
        creatorId, ProjectStatus.ABGESCHLOSSEN);
  }
}
