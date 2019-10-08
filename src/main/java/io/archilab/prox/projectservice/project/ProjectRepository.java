package io.archilab.prox.projectservice.project;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public interface ProjectRepository
    extends PagingAndSortingRepository<Project, UUID>, ProjectRepositoryCustom {

  Set<Project> findByStatus(@Param(value = "status") ProjectStatus status);

  Set<Project> findBySupervisorName_SupervisorNameContaining(
      @Param(value = "supervisorName") String supervisorName);

  Set<Project> findAllByModifiedAfter(
      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Param(value = "modified") Date modified);
}
