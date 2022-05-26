package de.innovationhub.prox.projectservice.module;


import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends CrudRepository<Specialization, UUID> {
  Set<Specialization> findAllByKeyIn(Collection<String> keys);
}
