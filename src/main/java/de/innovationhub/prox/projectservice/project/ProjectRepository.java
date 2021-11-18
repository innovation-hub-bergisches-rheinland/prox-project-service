package de.innovationhub.prox.projectservice.project;


import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

@RepositoryRestResource
public interface ProjectRepository
    extends PagingAndSortingRepository<Project, UUID>, ProjectRepositoryCustom {

  Set<Project> findByStatus(@Param(value = "status") ProjectStatus status);

  Set<Project> findBySupervisorName_SupervisorNameContaining(
      @Param(value = "supervisorName") String supervisorName);

  Set<Project> findAllByModifiedAfter(
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param(value = "modified") Date modified);

  @RestResource(exported = false)
  Set<Project> findAllByCreatorID_CreatorIDAndStatusIn(
      final UUID creatorId, ProjectStatus... status);
}
