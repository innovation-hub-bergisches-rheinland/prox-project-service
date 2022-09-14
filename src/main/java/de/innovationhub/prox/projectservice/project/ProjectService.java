package de.innovationhub.prox.projectservice.project;


import de.innovationhub.prox.projectservice.core.event.EventPublisher;
import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectFromProposal;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectCollectionDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectDto;
import de.innovationhub.prox.projectservice.project.event.ProjectChanged;
import de.innovationhub.prox.projectservice.project.event.ProjectDeleted;
import de.innovationhub.prox.projectservice.project.event.ProposalPromotedToProject;
import de.innovationhub.prox.projectservice.project.exception.ProjectNotFoundException;
import de.innovationhub.prox.projectservice.project.mapper.ProjectMapper;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
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
  private final EventPublisher eventPublisher;

  @Autowired
  public ProjectService(
    ProjectRepository projectRepository,
    SpecializationRepository specializationRepository,
    ModuleTypeRepository moduleTypeRepository,
    UserRepository userRepository,
    OrganizationRepository organizationRepository,
    ProjectMapper projectMapper,
    EventPublisher eventPublisher
  ) {
    this.projectRepository = projectRepository;
    this.specializationRepository = specializationRepository;
    this.moduleTypeRepository = moduleTypeRepository;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
    this.projectMapper = projectMapper;
    this.eventPublisher = eventPublisher;
  }

  public ReadProjectCollectionDto getAll() {
    var all = projectRepository.findAll();
    return projectMapper.toDto(StreamSupport.stream(all.spliterator(), false).toList());
  }

  public ReadProjectDto get(UUID projectId) {
    var project = getOrThrow(projectId);

    return projectMapper.toDto(project);
  }

  @Transactional(TxType.REQUIRED)
  public ReadProjectDto createForOrganization(UUID organizationId, CreateProjectDto projectDto) {
    // Assumption is that every organization that enters the service is valid and already verified
    // by the security filter chain.
    var org =
      this.organizationRepository
        .findById(organizationId)
        .orElseThrow();
    return create(projectDto, org);
  }

  @Transactional(TxType.REQUIRED)
  public ReadProjectDto createForUser(UUID userId, CreateProjectDto projectDto) {
    // Assumption is that every user that enters the service is valid and already verified
    // by the security filter chain.

    // We know that when a user is authenticated, it also exists. So we should create it here.
    // Profile information will eventually be added.
    var user = this.userRepository.findById(userId)
      .orElseGet(() -> new User(userId, "UNKNWON"));
    return create(projectDto, user);
  }

  // TODO: REALLY REALLY POORLY WRITTEN TEST (As everything else here)
  //  Do it properly once abstractions for eventing are set.
  @Transactional(TxType.REQUIRED)
  public ReadProjectDto createProjectFromProposal(CreateProjectFromProposal proposalDto) {
    var proposal = proposalDto.proposal();
    var project = new Project();
    project.setName(proposal.getName());
    project.setOwner(proposal.getOwner());
    project.setShortDescription(proposal.getDescription());
    project.setStatus(ProjectStatus.AVAILABLE);
    project.setModules(proposal.getModules());
    project.setSpecializations(proposal.getSpecializations());
    project.setRequirement(proposal.getRequirement());
    project.setProposalId(proposal.getId());

    // Profile information will eventually be added.
    // The user should already be present
    var supervisorUser = this.userRepository.findById(proposal.getCommittedSupervisor())
      .orElseGet(() -> new User(proposal.getCommittedSupervisor(), "UNKNWON"));

    var supervisor = new Supervisor(supervisorUser.getId(), supervisorUser.getName());
    project.setSupervisors(List.of(supervisor));
    project = saveAndPublish(project);

    var event = new ProposalPromotedToProject(proposal.getId(), project.getId());
    this.eventPublisher.publish(event);

    return projectMapper.toDto(project);
  }

  @Transactional(TxType.REQUIRED)
  public ReadProjectDto create(CreateProjectDto projectDto, AbstractOwner owner) {
    var project = projectMapper.toEntity(projectDto);
    project.setOwner(owner);
    project = saveAndPublish(project);
    return projectMapper.toDto(project);
  }

  @Transactional(TxType.REQUIRED)
  public ReadProjectDto update(UUID projectId, CreateProjectDto projectDto) {
    var project = getOrThrow(projectId);

    // Assumption is that permissions are already verified by the security filter chain
    projectMapper.updateProject(project, projectDto);

    project = saveAndPublish(project);
    return projectMapper.toDto(project);
  }

  public void delete(UUID projectId) {
    var project = getOrThrow(projectId);

    // Assumption is that permissions are already verified by the security filter chain
    projectRepository.delete(project);
    this.eventPublisher.publish(new ProjectDeleted(projectId));
  }

  @Transactional(TxType.REQUIRED)
  public ReadProjectDto setModuleTypes(UUID projectId, Collection<String> moduleTypeKeys) {
    var project = getOrThrow(projectId);
    var moduleTypes = this.moduleTypeRepository.findAllByKeyIn(moduleTypeKeys);
    // Assumption is that permissions are already verified by the security filter chain
    project.setModules(moduleTypes);
    project = saveAndPublish(project);
    return projectMapper.toDto(project);
  }

  @Transactional(TxType.REQUIRED)
  public ReadProjectDto setSpecializations(UUID projectId, Collection<String> specializationKeys) {
    var project = getOrThrow(projectId);
    var specializations = this.specializationRepository.findAllByKeyIn(specializationKeys);
    // Assumption is that permissions are already verified by the security filter chain
    project.setSpecializations(specializations);
    project = saveAndPublish(project);
    return projectMapper.toDto(project);
  }

  public ReadProjectCollectionDto filter(
      ProjectStatus status,
      String[] specializationKeys,
      String[] moduleTypeKeys,
      String text,
      Sort sort) {
    var projects =
        projectRepository.filterProjects(status, specializationKeys, moduleTypeKeys, text, sort);
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
    return this.projectRepository
      .findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(projectId));
  }

  @Transactional
  public ReadProjectCollectionDto reconcile(List<UUID> projectIds) {
    var projects = projectRepository.findAllByIdIn(projectIds);
    // just resave is enough to publish the changes
    for (var project : projects) {
      saveAndPublish(project);
    }
    return projectMapper.toDto(projects);
  }

  private Project saveAndPublish(Project project) {
    var saved = this.projectRepository.save(project);
    this.eventPublisher.publish(new ProjectChanged(saved));
    return saved;
  }
}
