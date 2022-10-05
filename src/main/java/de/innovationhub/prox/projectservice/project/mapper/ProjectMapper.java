package de.innovationhub.prox.projectservice.project.mapper;


import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.Supervisor;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.dto.CreateSupervisorDto;
import de.innovationhub.prox.projectservice.project.dto.ProjectPermissionsDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectCollectionDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectDto;
import de.innovationhub.prox.projectservice.project.dto.ReadSupervisorDto;
import de.innovationhub.prox.projectservice.security.OwnablePermissionEvaluatorHelper;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper {
  public static final ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

  @Autowired private OwnablePermissionEvaluatorHelper<Project> permissionEvaluatorHelper;

  public ProjectPermissionsDto getPermissions(Project project) {
    var hasPermission = permissionEvaluatorHelper.hasPermissionWithCurrentContext(project);
    return new ProjectPermissionsDto(hasPermission, hasPermission);
  }

  @Mapping(target = "permissions", expression = "java( this.getPermissions(project) )")
  public abstract ReadProjectDto toDto(Project project);

  public abstract ReadSupervisorDto toDto(Supervisor supervisor);

  public ReadProjectCollectionDto toDto(List<Project> projects) {
    return new ReadProjectCollectionDto(
        projects.stream().map(this::toDto).collect(Collectors.toList()));
  }

  public abstract Project toEntity(CreateProjectDto projectDto);

  public abstract Supervisor toEntity(CreateSupervisorDto supervisorDto);

  public void updateProject(Project projectToUpdate, CreateProjectDto updated) {
    if (updated == null) {
      return;
    }

    projectToUpdate.setName(updated.name());
    projectToUpdate.setDescription(updated.description());
    projectToUpdate.setShortDescription(updated.shortDescription());
    projectToUpdate.setRequirement(updated.requirement());
    projectToUpdate.setStatus(updated.status());
    projectToUpdate.setCreatorName(updated.creatorName());

    if (projectToUpdate.getSupervisors() != null) {
      projectToUpdate.setSupervisors(
          updated.supervisors().stream().map(this::toEntity).collect(Collectors.toList()));
    }
  }
}
