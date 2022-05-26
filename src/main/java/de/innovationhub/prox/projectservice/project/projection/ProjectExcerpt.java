package de.innovationhub.prox.projectservice.project.projection;


import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import java.time.Instant;
import java.util.Set;
import org.springframework.data.rest.core.config.Projection;

@Projection(
    name = "withAssociations",
    types = {Project.class})
public interface ProjectExcerpt {

  String getId();

  String getName();

  String getDescription();

  String getShortDescription();

  String getRequirement();

  ProjectStatus getStatus();

  AbstractOwner getOwner();

  String getCreatorName();

  String getSupervisorName();

  Set<ModuleType> getModules();

  Set<Specialization> getSpecializations();

  Instant getCreatedAt();

  Instant getModifiedAt();
}
