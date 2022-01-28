package de.innovationhub.prox.projectservice.project.infrastructure.persistence;


import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository
    extends JpaRepository<ProjectJpaEntity, UUID> {
  /*Set<ProjectJpaEntity> findByStatus(ProjectStatus status);

  Set<ProjectJpaEntity> findBySupervisorName_SupervisorNameContaining(String supervisorName);

  Set<ProjectJpaEntity> findAllByModifiedAfter(Date modified);

  Set<ProjectJpaEntity> findAllByCreatorID_CreatorIDAndStatusIn(
      final UUID creatorId, ProjectStatus... status);*/
}
