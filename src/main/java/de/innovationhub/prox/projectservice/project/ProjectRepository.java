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

  @Query(
    "select p from Project p where p.owner.id = ?1 and p.owner.ownerType = ?2 order by p.modifiedAt desc")
  List<Project> findByOwner(UUID id, String discriminator);

  @Query("select p from Project p where p.id in ?1 order by p.modifiedAt desc")
  List<Project> findAllByIdIn(Collection<UUID> ids);

  /*@Query(value = """
          SELECT p.*
          FROM project p
                   LEFT JOIN (SELECT pt.project_id AS id, array_agg(pt.tags) AS tag_array
                              FROM project_tags pt
                              GROUP BY pt.project_id) t USING (id)
                   LEFT JOIN project_supervisors ps on p.id = ps.project_id
                   LEFT JOIN abstract_owner ao on p.owner_id = ao.id
                   LEFT JOIN project_specializations s on p.id = s.project_id
                   LEFT JOIN specializations s2 on s.specializations_id = s2.id
                   LEFT JOIN project_modules pm on p.id = pm.project_id
                   LEFT JOIN module_type m on pm.modules_id = m.id
          WHERE p.status = :status
            AND s2.key IN (:specializationKeys)
            AND m.key IN (:moduleTypeKeys)
            AND (
                  concat_ws(' ', p.name, p.short_description, p.description, p.requirement, ps.name, ao.owner_name,
                            array_to_string(tag_array, ' ')) similar to
                  ('%(' || lower(REGEXP_REPLACE(coalesce(:text, ''), '\\s+', '|')) || ')%')
              )
          ORDER BY p.modified_at DESC;
    """, nativeQuery = true)
  List<Project> filterProjects(
    @Nullable @Param("status") ProjectStatus status,
    @Nullable @Param("specializationKeys") String[] specializationKeys,
    @Nullable @Param("moduleTypeKeys") String[] moduleTypeKeys,
    @Nullable @Param("text") String text);

  default List<Project> search(String query) {
    return filterProjects(null, null, null, query);
  }*/
}
