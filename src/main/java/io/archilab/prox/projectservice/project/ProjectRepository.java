package io.archilab.prox.projectservice.project;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends PagingAndSortingRepository<Project, UUID> {

  Set<Project> findByStatus(@Param(value = "status") ProjectStatus status);

  Set<Project> findBySupervisorName_SupervisorNameContaining(
      @Param(value = "supervisorName") String supervisorName);
}
