package de.innovationhub.prox.projectservice.project.projection;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.StudyProgram;
import de.innovationhub.prox.projectservice.project.CreatorID;
import de.innovationhub.prox.projectservice.project.CreatorName;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectContext;
import de.innovationhub.prox.projectservice.project.ProjectDescription;
import de.innovationhub.prox.projectservice.project.ProjectName;
import de.innovationhub.prox.projectservice.project.ProjectRequirement;
import de.innovationhub.prox.projectservice.project.ProjectShortDescription;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import de.innovationhub.prox.projectservice.project.SupervisorName;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "withModules", types = {Project.class})
public interface ProjectWithModulesProjection {
  @Value("#{target.name.name}")
  String getName();
  @Value("#{target.description.description}")
  String getDescription();
  @Value("#{target.shortDescription.shortDescription}")
  String getShortDescription();
  @Value("#{target.requirement.requirement}")
  String getRequirement();
  ProjectStatus getStatus();
  ProjectContext getContext();
  @Value("#{target.creatorID.creatorID}")
  String getCreatorID();
  @Value("#{target.creatorName.creatorName}")
  String getCreatorName();
  @Value("#{target.supervisorName.supervisorName}")
  String getSupervisorName();
  Set<ModuleType> getModules();
  java.util.Date getCreated();
  java.util.Date getModified();
}
