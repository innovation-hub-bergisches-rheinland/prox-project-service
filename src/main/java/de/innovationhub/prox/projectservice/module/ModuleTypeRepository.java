package de.innovationhub.prox.projectservice.module;


import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleTypeRepository extends CrudRepository<ModuleType, UUID> {

  Set<ModuleType> findAllByKeyIn(Collection<String> keys);

  @Query("select m from StudyProgram s join s.modules m where s.specialization.key IN ?1")
  Set<ModuleType> findAllModuleTypesOfSpecializationId(Set<String> keys);

  @Query(
      "select s from StudyProgram sp join sp.specialization s join sp.modules mt where mt.key IN ?1")
  Set<Specialization> findSpecializationsOfModules(Set<String> keys);
}
