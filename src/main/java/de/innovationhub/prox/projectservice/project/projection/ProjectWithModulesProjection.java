package de.innovationhub.prox.projectservice.project.projection;


import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectContext;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.rest.core.config.Projection;

@Projection(
    name = "withAssociations",
    types = {Project.class})
public interface ProjectWithModulesProjection {

  String getId();

  String getName();

  String getDescription();

  String getShortDescription();

  String getRequirement();

  ProjectStatus getStatus();

  ProjectContext getContext();

  UUID getCreatorID();

  String getCreatorName();

  String getSupervisorName();

  Set<ModuleType> getModules();

  Set<Specialization> getSpecializations();

  Instant getCreatedAt();

  Instant getModifiedAt();
}
