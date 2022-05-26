package de.innovationhub.prox.projectservice.project;


import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository
    extends PagingAndSortingRepository<Project, UUID>, ProjectRepositoryCustom {
  @Query("select p from Project p where p.owner.id = ?1 and p.owner.ownerType = ?2")
  List<Project> findByOwner(UUID id, String discriminator);
}
