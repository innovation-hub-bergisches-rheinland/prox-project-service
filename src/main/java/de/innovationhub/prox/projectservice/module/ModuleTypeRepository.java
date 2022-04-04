package de.innovationhub.prox.projectservice.module;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "modules")
@RepositoryRestResource
public interface ModuleTypeRepository extends CrudRepository<ModuleType, UUID> {

  @Query("select m from StudyProgram s join s.modules m where s.specialization.id IN :ids")
  Set<ModuleType> findAllModuleTypesOfSpecializationId(
      @Param("ids")
          @Parameter(
              array =
                  @ArraySchema(
                      uniqueItems = true,
                      schema = @Schema(name = "string", type = "uuid")))
          Set<UUID> ids);

  @Query(
      "select s from StudyProgram sp join sp.specialization s join sp.modules mt where mt.id IN :ids")
  Set<Specialization> findSpecializationsOfModules(
      @Param("ids")
          @Parameter(
              array =
                  @ArraySchema(
                      uniqueItems = true,
                      schema = @Schema(name = "string", type = "uuid")))
          Set<UUID> ids);
}
