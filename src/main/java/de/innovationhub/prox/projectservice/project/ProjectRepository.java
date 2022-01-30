package de.innovationhub.prox.projectservice.project;


import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;

@Tag(name = "projects")
@RepositoryRestResource
public interface ProjectRepository
    extends PagingAndSortingRepository<Project, UUID>, ProjectRepositoryCustom {

  Set<Project> findByStatus(@Param(value = "status") ProjectStatus status);

  Set<Project> findBySupervisorNameContaining(
      @Param(value = "supervisorName") String supervisorName);

  Set<Project> findAllByModifiedAtAfter(
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param(value = "modified") Instant modified);

  @RestResource(exported = false)
  Set<Project> findAllByCreatorIDAndStatusIn(final UUID creatorId, ProjectStatus... status);
}
