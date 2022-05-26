package de.innovationhub.prox.projectservice.project;


import de.innovationhub.prox.projectservice.project.projection.ProjectExcerpt;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "projects")
@RepositoryRestResource(excerptProjection = ProjectExcerpt.class)
public interface ProjectRepository
    extends PagingAndSortingRepository<Project, UUID>, ProjectRepositoryCustom {

  @Override
  Project save(Project entity);

  // 's' for backwards compatibility
  @RestResource(path = "findAllByIdsIn")
  List<Project> findAllByIdIn(@RequestParam("projectIds") UUID[] projectIds);

  List<Project> findByStatus(@Param(value = "status") ProjectStatus status, Sort sort);

  List<Project> findBySupervisorNameContaining(
      @Param(value = "supervisorName") String supervisorName, Sort sort);

  List<Project> findAllByModifiedAtAfter(
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param(value = "modified") Instant modified,
      Sort sort);

  @RestResource(exported = false)
  List<Project> findAllByOwner_IdAndStatusIn(
      final UUID creatorId, Iterable<ProjectStatus> status, Sort sort);
}
