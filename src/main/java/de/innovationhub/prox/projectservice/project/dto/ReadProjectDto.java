package de.innovationhub.prox.projectservice.project.dto;


import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import java.time.Instant;
import java.util.List;
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
  List<ReadSupervisorDto> supervisors,
  Set<Specialization> specializations,
  Set<ModuleType> modules,
  List<String> tags,
  AbstractOwner owner,
  UUID proposalId,
  Instant createdAt,
  Instant modifiedAt,
  ProjectPermissionsDto permissions) {}
