package de.innovationhub.prox.projectservice.project.infrastructure.persistence;

import de.innovationhub.prox.projectservice.project.domain.Project;
import de.innovationhub.prox.projectservice.project.domain.Project.ProjectId;
import de.innovationhub.prox.projectservice.project.domain.ProjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepositoryJpaImpl implements ProjectRepository {
  private final ProjectJpaMapper projectJpaMapper;
  private final ProjectJpaRepository projectJpaRepository;

  @Autowired
  public ProjectRepositoryJpaImpl(
      ProjectJpaMapper projectJpaMapper,
      ProjectJpaRepository projectJpaRepository) {
    this.projectJpaMapper = projectJpaMapper;
    this.projectJpaRepository = projectJpaRepository;
  }

  @Override
  public List<Project> findAll() {
    return this.projectJpaRepository.findAll()
        .stream().map(this.projectJpaMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Project> findProjectById(ProjectId id) {
    return this.projectJpaRepository.findById(id.id())
        .map(this.projectJpaMapper::toDomain);
  }

  @Override
  @Transactional
  public Project save(Project project) {
    var jpaEntity = this.projectJpaMapper.toPersistence(project);
    var savedEntity = this.projectJpaRepository.save(jpaEntity);
    return this.projectJpaMapper.toDomain(savedEntity);
  }

  @Override
  @Transactional
  public void delete(ProjectId projectId) {
    this.projectJpaRepository.deleteById(projectId.id());
  }
}
