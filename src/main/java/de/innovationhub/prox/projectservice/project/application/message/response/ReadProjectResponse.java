package de.innovationhub.prox.projectservice.project.application.message.response;

import de.innovationhub.prox.projectservice.project.domain.ContextDefinition;
import de.innovationhub.prox.projectservice.project.domain.ProjectStatus;
import java.util.UUID;

public record ReadProjectResponse(
    UUID id,
    String name,
    String description,
    String shortDescription,
    String requirement,
    ProjectStatus status,
    String supervisorName
) {

}
