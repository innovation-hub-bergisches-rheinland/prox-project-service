package de.innovationhub.prox.projectservice.module;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ModuleTypeRepository extends CrudRepository<ModuleType, UUID> {

}