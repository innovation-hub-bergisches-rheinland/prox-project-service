package de.innovationhub.prox.projectservice.project;


import de.innovationhub.prox.projectservice.project.projection.ProjectExcerpt;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
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

  @Query("select p from Project p where p.owner.id = ?1 and p.owner.ownerType = ?2")
  List<ProjectExcerpt> findByOwner(UUID id, String discriminator);

  List<Project> findByStatus(@Param(value = "status") ProjectStatus status, Sort sort);
}
