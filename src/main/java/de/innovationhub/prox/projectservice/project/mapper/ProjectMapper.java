package de.innovationhub.prox.projectservice.project.mapper;


import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.dto.ProjectPermissionsDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectCollectionDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectDto;
import de.innovationhub.prox.projectservice.security.ProjectPermissionEvaluatorHelper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProjectMapper {
  @Autowired private ProjectPermissionEvaluatorHelper permissionEvaluatorHelper;

  public ProjectPermissionsDto getPermissions(Project project) {
    var hasPermission = permissionEvaluatorHelper.hasPermissionWithCurrentContext(project);
    return new ProjectPermissionsDto(hasPermission, hasPermission);
  }

  @Mapping(target = "permissions", expression = "java( this.getPermissions(project) )")
  public abstract ReadProjectDto toDto(Project project);

  public ReadProjectCollectionDto toDto(List<Project> projects) {
    return new ReadProjectCollectionDto(projects.stream().map(this::toDto).toList());
  }

  public abstract Project toEntity(CreateProjectDto projectDto);

  public abstract void updateProject(
      @MappingTarget Project projectToUpdate, CreateProjectDto updated);
}
