package de.innovationhub.prox.projectservice.module;


import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Tag(name = "specializations")
@RepositoryRestResource
public interface SpecializationRepository extends CrudRepository<Specialization, UUID> {}
