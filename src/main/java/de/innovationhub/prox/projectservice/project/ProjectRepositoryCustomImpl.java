package de.innovationhub.prox.projectservice.project;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

  private final EntityManager entityManager;

  @Autowired
  public ProjectRepositoryCustomImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<Project> filterProjects(
    ProjectStatus status,
    Collection<String> specializationKeys,
    Collection<String> moduleTypeKeys,
    String text) {
    var searchQuery = """
        SELECT DISTINCT p.*, ts_rank(document, query) AS rank
        FROM project p
                 LEFT JOIN (SELECT pt.project_id AS id, array_agg(pt.tags) AS tag_array
                            FROM project_tags pt
                            GROUP BY pt.project_id) tag_array USING (id)
                 LEFT JOIN project_supervisors ps on p.id = ps.project_id
                 LEFT JOIN abstract_owner ao on p.owner_id = ao.id
                 LEFT JOIN project_specializations s on p.id = s.project_id
                 LEFT JOIN specializations s2 on s.specializations_id = s2.id
                 LEFT JOIN project_modules pm on p.id = pm.project_id
                 LEFT JOIN module_type m on pm.modules_id = m.id,
              to_tsvector('simple', concat_ws(' ', p.name, p.short_description, p.requirement, ao.owner_name, ps.name,
                            array_to_string(tag_array, ' '))) document,
              to_tsquery('simple', REGEXP_REPLACE(lower(:query), '\\s+', ':* & ', 'g')) query
        WHERE (:status IS NULL OR p.status = :status)
            AND (:specializationKeys IS NULL OR s2.key IN (:specializationKeys))
            AND (:moduleTypeKeys IS NULL OR m.key IN (:moduleTypeKeys))
            AND (:query <> '' IS NOT TRUE OR
                  document @@ query
              )
        ORDER BY rank DESC, p.modified_at DESC;
      """;

    var nativeQuery = entityManager.createNativeQuery(searchQuery, Project.class);
    nativeQuery.setParameter("query", new TypedParameterValue(StandardBasicTypes.STRING, text));
    nativeQuery.setParameter("status",
      new TypedParameterValue(StandardBasicTypes.INTEGER,
        status != null ? status.ordinal() : null));
    nativeQuery.setParameter("specializationKeys",
      specializationKeys != null ? specializationKeys : new ArrayList<String>());
    nativeQuery.setParameter("moduleTypeKeys",
      moduleTypeKeys != null ? moduleTypeKeys : new ArrayList<String>());

    return (List<Project>) nativeQuery.getResultList();
  }

  @Override
  public List<Project> search(String query) {
    return filterProjects(null, null, null, query);
  }
}
