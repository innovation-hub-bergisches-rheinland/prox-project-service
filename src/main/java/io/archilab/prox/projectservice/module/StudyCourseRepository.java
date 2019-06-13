package io.archilab.prox.projectservice.module;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "projectStudyCourses", path = "projectStudyCourses")
public interface StudyCourseRepository extends PagingAndSortingRepository<StudyCourse, UUID> {

  List<StudyCourse> findByAcademicDegree(AcademicDegree academicDegree);

  Optional<StudyCourse> findByExternalStudyCourseID(ExternalStudyCourseID externalStudyCourseID);
}
