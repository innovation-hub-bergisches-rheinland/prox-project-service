package io.archilab.projektboerse.projectservice.project;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

public interface ProjectRepository extends PagingAndSortingRepository<Project, UUID> {

  Set<Project> findByStatus(@Param(value="status") ProjectStatus status);
  List<Project> findByCreatorName(@Param(value="creatorName") String creatorName);
  
  // save, saveAll    delete, deleteAll, deleteAll, deleteById,

}
