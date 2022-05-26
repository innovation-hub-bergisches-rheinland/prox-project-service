package de.innovationhub.prox.projectservice.project.dto;

import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import java.time.Instant;
import java.util.HashSet;

public record CreateProjectDto(
    String name,
    String description,
    String shortDescription,
    String requirement,
    ProjectStatus status,
    String creatorName,
    String supervisorName
) {
}
