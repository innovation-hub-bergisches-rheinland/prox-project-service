/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.module.ModuleType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Streamable;
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
    return projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(creatorId, ProjectStatus.VERFÜGBAR);
  }

  @Override
  public Set<Project> findRunningAndFinishedProjectsOfCreator(UUID creatorId) {
    return projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(creatorId, ProjectStatus.ABGESCHLOSSEN, ProjectStatus.LAUFEND);
  }

  @Override
  public Set<Project> filterProjects(ProjectStatus status, String[] moduleTypeKeys, String text) {
    //TODO refactor and use fuzzy search or something similar which does not require hardcoding
    return StreamSupport.stream(this.projectRepository.findAll().spliterator(), false)
        .filter(p -> status != null ? p.getStatus() == status : true)
        .filter(p -> moduleTypeKeys != null && moduleTypeKeys.length > 0 ? StreamSupport.stream(p.getModules().spliterator(), false)
            .anyMatch(m -> Arrays.stream(moduleTypeKeys).anyMatch(k -> k.equalsIgnoreCase(m.getKey()))) : true)
        .filter(p -> text != null && text.length() > 0 ? p.getCreatorName().getCreatorName().toLowerCase().contains(text.toLowerCase()) ||
             p.getDescription().getDescription().toLowerCase().contains(text.toLowerCase()) ||
            p.getShortDescription().getShortDescription().toLowerCase().contains(text.toLowerCase()) ||
            p.getName().getName().toLowerCase().contains(text.toLowerCase()) ||
            p.getRequirement().getRequirement().toLowerCase().contains(text.toLowerCase()) ||
            p.getSupervisorName().getSupervisorName().toLowerCase().contains(text.toLowerCase()) : true)
        .collect(Collectors.toSet());
  }

  @Override
  public ProjectStats findProjectStatsOfCreator(UUID creatorId) {
    var projects = this.projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(creatorId, ProjectStatus.LAUFEND, ProjectStatus.VERFÜGBAR, ProjectStatus.ABGESCHLOSSEN);
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
    return projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(creatorId, ProjectStatus.LAUFEND);
  }

  @Override
  public Set<Project> findinishedProjectsOfCreator(UUID creatorId) {
    return projectRepository.findAllByCreatorID_CreatorIDAndStatusIn(creatorId, ProjectStatus.ABGESCHLOSSEN);
  }
}
