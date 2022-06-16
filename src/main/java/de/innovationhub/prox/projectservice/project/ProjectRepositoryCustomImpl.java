package de.innovationhub.prox.projectservice.project;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;

public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

  @Autowired @Lazy private ProjectRepository projectRepository;

  @Override
  public List<Project> filterProjects(
      ProjectStatus status,
      String[] specializationKeys,
      String[] moduleTypeKeys,
      String text,
      Sort sort) {
    // TODO refactor and use fuzzy search or something similar which does not require hardcoding
    return StreamSupport.stream(this.projectRepository.findAll(sort).spliterator(), false)
        .filter(p -> status == null || p.getStatus() == status)
        .filter(
            p ->
                specializationKeys == null
                    || specializationKeys.length <= 0
                    || p.getSpecializations().stream()
                        .anyMatch(
                            s ->
                                Arrays.stream(specializationKeys)
                                    .anyMatch(k -> k.equalsIgnoreCase(s.getKey()))))
        .filter(
            p ->
                moduleTypeKeys == null
                    || moduleTypeKeys.length <= 0
                    || p.getModules().stream()
                        .anyMatch(
                            m ->
                                Arrays.stream(moduleTypeKeys)
                                    .anyMatch(k -> k.equalsIgnoreCase(m.getKey()))))
        .filter(
            p -> {
              if (text == null || text.length() <= 0) {
                return true;
              }
              var match = false;
              if (p.getCreatorName() != null) {
                match |= p.getCreatorName().toLowerCase().contains(text.toLowerCase());
              }
              if (!match && p.getDescription() != null) {
                match |= p.getDescription().toLowerCase().contains(text.toLowerCase());
              }
              if (!match && p.getShortDescription() != null) {
                match |= p.getShortDescription().toLowerCase().contains(text.toLowerCase());
              }
              if (!match && p.getName() != null) {
                match |= p.getName().toLowerCase().contains(text.toLowerCase());
              }
              if (!match && p.getRequirement() != null) {
                match |= p.getRequirement().toLowerCase().contains(text.toLowerCase());
              }
              if (!match && p.getSupervisors() != null) {
                match |=
                    p.getSupervisors().stream()
                        .anyMatch(s -> s.getName().toLowerCase().contains(text.toLowerCase()));
              }
              return match;
            })
        .collect(Collectors.toList());
  }
}
