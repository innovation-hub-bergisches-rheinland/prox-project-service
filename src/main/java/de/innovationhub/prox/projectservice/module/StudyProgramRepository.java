package de.innovationhub.prox.projectservice.module;


import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyProgramRepository
    extends CrudRepository<StudyProgram, UUID> {
  @Query("select distinct m from StudyProgram sp join sp.modules m where sp.id in ?1")
  List<ModuleType> findAllModulesOfStudyPrograms(List<UUID> studyProgramIds);
}
