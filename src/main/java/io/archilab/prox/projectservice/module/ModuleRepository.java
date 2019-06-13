package io.archilab.prox.projectservice.module;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "projectModules", path = "projectModules")
public interface ModuleRepository extends PagingAndSortingRepository<Module, UUID> {

  Optional<Module> findByExternalModuleID(ExternalModuleID externalModuleID);
}

