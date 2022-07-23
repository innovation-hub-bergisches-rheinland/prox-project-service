package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectCollectionDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectDto;
import de.innovationhub.prox.projectservice.project.exception.ProjectNotFoundException;
import de.innovationhub.prox.projectservice.project.mapper.ProjectMapper;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
  private final ProjectRepository projectRepository;
  private final SpecializationRepository specializationRepository;
  private final ModuleTypeRepository moduleTypeRepository;
  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;
  private final ProjectMapper projectMapper;

  @Autowired
  public ProjectService(ProjectRepository projectRepository,
      SpecializationRepository specializationRepository, ModuleTypeRepository moduleTypeRepository,
      UserRepository userRepository, OrganizationRepository organizationRepository,
      ProjectMapper projectMapper) {
    this.projectRepository = projectRepository;
    this.specializationRepository = specializationRepository;
    this.moduleTypeRepository = moduleTypeRepository;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
    this.projectMapper = projectMapper;
  }

  public ReadProjectCollectionDto getAll() {
    var all = projectRepository.findAll();
    return projectMapper.toDto(StreamSupport.stream(all.spliterator(), false).toList());
  }

  public ReadProjectDto get(UUID projectId) {
    var project = getOrThrow(projectId);

    return projectMapper.toDto(project);
  }

  public ReadProjectDto createForOrganization(UUID organizationId, CreateProjectDto projectDto) {
    // Assumption is that every organization that enters the service is valid and already verified
    // by the security filter chain.
    var org =
        this.organizationRepository
            .findById(organizationId)
            .orElse(organizationRepository.save(new Organization(organizationId)));
    return create(projectDto, org);
  }

  public ReadProjectDto createForUser(UUID userId, CreateProjectDto projectDto) {
    // Assumption is that every user that enters the service is valid and already verified
    // by the security filter chain.
    var user = this.userRepository.findById(userId).orElse(userRepository.save(new User(userId)));
    return create(projectDto, user);
  }

  private ReadProjectDto create(CreateProjectDto projectDto, AbstractOwner owner) {
    var project = projectMapper.toEntity(projectDto);
    project.setOwner(owner);
    project = projectRepository.save(project);
    return projectMapper.toDto(project);
  }

  public ReadProjectDto update(UUID projectId, CreateProjectDto projectDto) {
    var project = getOrThrow(projectId);

    // Assumption is that permissions are already verified by the security filter chain
    projectMapper.updateProject(project, projectDto);

    project = projectRepository.save(project);
    return projectMapper.toDto(project);
  }

  public void delete(UUID projectId) {
    var project = getOrThrow(projectId);

    // Assumption is that permissions are already verified by the security filter chain
    projectRepository.delete(project);
  }

  public ReadProjectDto setModuleTypes(UUID projectId, Collection<String> moduleTypeKeys) {
    var project = getOrThrow(projectId);
    var moduleTypes = this.moduleTypeRepository.findAllByKeyIn(moduleTypeKeys);
    // Assumption is that permissions are already verified by the security filter chain
    project.setModules(moduleTypes);
    project = projectRepository.save(project);
    return projectMapper.toDto(project);
  }

  public ReadProjectDto setSpecializations(UUID projectId, Collection<String> specializationKeys) {
    var project = getOrThrow(projectId);
    var specializations = this.specializationRepository.findAllByKeyIn(specializationKeys);
    // Assumption is that permissions are already verified by the security filter chain
    project.setSpecializations(specializations);
    project = projectRepository.save(project);
    return projectMapper.toDto(project);
  }

  public ReadProjectCollectionDto filter(ProjectStatus status, String[] specializationKeys, String[] moduleTypeKeys, String text, Sort sort) {
    var projects = projectRepository.filterProjects(status, specializationKeys, moduleTypeKeys, text, sort);
    return projectMapper.toDto(projects.stream().toList());
  }

  public ReadProjectCollectionDto findByUser(UUID id) {
    var projects = projectRepository.findByOwner(id, User.DISCRIMINATOR);
    return projectMapper.toDto(projects.stream().toList());
  }

  public ReadProjectCollectionDto findByOrg(UUID id) {
    var projects = projectRepository.findByOwner(id, Organization.DISCRIMINATOR);
    return projectMapper.toDto(projects.stream().toList());
  }

  private Project getOrThrow(UUID projectId) {
    return this.projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
  }
}
