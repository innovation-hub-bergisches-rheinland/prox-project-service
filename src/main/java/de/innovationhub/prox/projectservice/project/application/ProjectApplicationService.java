package de.innovationhub.prox.projectservice.project.application;

import de.innovationhub.prox.projectservice.project.application.message.ProjectMessageMapper;
import de.innovationhub.prox.projectservice.project.application.message.request.CreateProjectRequest;
import de.innovationhub.prox.projectservice.project.application.message.request.UpdateProjectRequest;
import de.innovationhub.prox.projectservice.project.application.message.response.ReadProjectResponse;
import de.innovationhub.prox.projectservice.project.domain.Project.ProjectId;
import de.innovationhub.prox.projectservice.project.domain.ProjectContext;
import de.innovationhub.prox.projectservice.project.domain.ProjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectApplicationService {
  private final ProjectRepository projectRepository;
  private final ProjectMessageMapper projectMessageMapper;

  @Autowired
  public ProjectApplicationService(
      ProjectRepository projectRepository,
      ProjectMessageMapper projectMessageMapper) {
    this.projectRepository = projectRepository;
    this.projectMessageMapper = projectMessageMapper;
  }

  public List<ReadProjectResponse> findAll() {
    return this.projectRepository.findAll()
        .stream().map(this.projectMessageMapper::toResponse)
        .collect(Collectors.toList());
  }

  public Optional<ReadProjectResponse> findById(UUID id) {
    return this.projectRepository.findProjectById(new ProjectId(id))
        .map(this.projectMessageMapper::toResponse);
  }

  public ReadProjectResponse save(CreateProjectRequest project, ProjectContext context) {
    var domain = this.projectMessageMapper.toDomain(project, context);
    var saved = this.projectRepository.save(domain);
    return this.projectMessageMapper.toResponse(saved);
  }

  public ReadProjectResponse update(UUID id, UpdateProjectRequest updateRequest) {
    var found = this.projectRepository.findProjectById(new ProjectId(id))
        .orElseThrow();
    var updated  = this.projectMessageMapper.updateProject(updateRequest, found);
    var saved = this.projectRepository.save(updated);
    return this.projectMessageMapper.toResponse(saved);
  }

  public void delete(UUID id) {
    this.projectRepository.delete(new ProjectId(id));
  }
}
