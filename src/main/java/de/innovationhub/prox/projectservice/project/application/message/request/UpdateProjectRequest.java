package de.innovationhub.prox.projectservice.project.application.message.request;

import de.innovationhub.prox.projectservice.project.domain.ContextDefinition;
import de.innovationhub.prox.projectservice.project.domain.ProjectStatus;

public record UpdateProjectRequest(
    String name,
    String description,
    String shortDescription,
    String requirement,
    ProjectStatus status,
    ContextDefinition context,
    String supervisorName
) {

}
