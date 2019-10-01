package io.archilab.prox.projectservice.project;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

public interface ProjectRepository extends PagingAndSortingRepository<Project, UUID> {

  Set<Project> findByStatus(@Param(value = "status") ProjectStatus status);

  Set<Project> findBySupervisorName_SupervisorNameContaining(
      @Param(value = "supervisorName") String supervisorName);

  Set<Project> findAllByModifiedAfter(
          @DateTimeFormat(pattern = "yyyy-MM-dd")
          @Param(value = "modified") Date modified);

}
