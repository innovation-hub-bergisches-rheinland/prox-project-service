package de.innovationhub.prox.projectservice.project.application.message.request;

import de.innovationhub.prox.projectservice.project.domain.ProjectStatus;

public record CreateProjectRequest(
    String name,
    String description,
    String shortDescription,
    String requirements,
    ProjectStatus status,
    String supervisorName
) {

}
