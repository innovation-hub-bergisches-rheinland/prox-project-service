package de.innovationhub.prox.projectservice.project.dto;

import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ReadProjectDto(
    UUID id,
    String name,
    String description,
    String shortDescription,
    String requirement,
    ProjectStatus status,
    String creatorName,
    String supervisorName,
    Set<Specialization> specializations,
    Set<ModuleType> modules,
    AbstractOwner owner,
    Instant createdAt,
    Instant modifiedAt,
    ProjectPermissionsDto permissions
) {
}
