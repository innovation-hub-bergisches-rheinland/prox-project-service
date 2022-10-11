package de.innovationhub.prox.projectservice.project;


import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository
  extends PagingAndSortingRepository<Project, UUID>, ProjectRepositoryCustom {

  default List<Project> findAll() {
    return StreamSupport.stream(findAll(Sort.by("modifiedAt").descending()).spliterator(), false)
      .toList();
  }

  @Query("""
    select p from Project p
      join AbstractOwner ow on ow.id = p.ownerId
      where ow.id = ?1 and ow.ownerType = ?2 order by p.modifiedAt desc
    """)
  List<Project> findByOwner(UUID id, String discriminator);

  @Query("select p from Project p where p.id in ?1 order by p.modifiedAt desc")
  List<Project> findAllByIdIn(Collection<UUID> ids);
}
