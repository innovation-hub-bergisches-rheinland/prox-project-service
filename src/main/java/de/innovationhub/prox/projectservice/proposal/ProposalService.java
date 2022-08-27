package de.innovationhub.prox.projectservice.proposal;


import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.project.ProjectService;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.dto.CreateSupervisorDto;
import de.innovationhub.prox.projectservice.project.dto.ReadProjectDto;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import de.innovationhub.prox.projectservice.proposal.exception.NoUsernameInAuthenticationException;
import de.innovationhub.prox.projectservice.proposal.exception.ProposalNotFoundException;
import de.innovationhub.prox.projectservice.proposal.exception.UnsupportedAuthenticationException;
import de.innovationhub.prox.projectservice.proposal.mapper.ProposalMapper;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ProposalService {
  private final ProposalRepository proposalRepository;
  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;
  private final ProposalMapper proposalMapper;
  private final ModuleTypeRepository moduleTypeRepository;
  private final SpecializationRepository specializationRepository;
  private final ProjectService projectService;

  @Autowired
  public ProposalService(
      ProposalRepository proposalRepository,
      UserRepository userRepository,
      OrganizationRepository organizationRepository,
      ProposalMapper proposalMapper,
      ModuleTypeRepository moduleTypeRepository,
      SpecializationRepository specializationRepository,
      ProjectService projectService) {
    this.proposalRepository = proposalRepository;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
    this.proposalMapper = proposalMapper;
    this.moduleTypeRepository = moduleTypeRepository;
    this.specializationRepository = specializationRepository;
    this.projectService = projectService;
  }

  public ReadProposalCollectionDto getAll() {
    var all = proposalRepository.findAll();
    return proposalMapper.toDto(StreamSupport.stream(all.spliterator(), false).toList());
  }

  public ReadProposalDto get(UUID proposalId) {
    var proposal = getOrThrow(proposalId);

    return proposalMapper.toDto(proposal);
  }

  public ReadProposalDto createForOrganization(
      UUID organizationId, CreateProposalDto proposalToCreate) {
    // Assumption is that every organization that enters the service is valid and already verified
    // by the security filter chain.
    var org =
        this.organizationRepository
            .findById(organizationId)
            .orElse(organizationRepository.save(new Organization(organizationId)));
    return create(proposalToCreate, org);
  }

  public ReadProposalDto createForUser(UUID userId, CreateProposalDto proposalToCreate) {
    // Assumption is that every user that enters the service is valid and already verified
    // by the security filter chain.
    var user = this.userRepository.findById(userId).orElse(userRepository.save(new User(userId)));
    return create(proposalToCreate, user);
  }

  private ReadProposalDto create(CreateProposalDto proposalToCreate, AbstractOwner owner) {
    var proposal = proposalMapper.toEntity(proposalToCreate);
    proposal.setOwner(owner);
    proposal = proposalRepository.save(proposal);
    return proposalMapper.toDto(proposal);
  }

  public ReadProposalDto update(UUID proposalId, CreateProposalDto updatedProposal) {
    var proposal = getOrThrow(proposalId);

    // Assumption is that permissions are already verified by the security filter chain
    proposalMapper.updateProposal(proposal, updatedProposal);

    proposal = proposalRepository.save(proposal);
    return proposalMapper.toDto(proposal);
  }

  public void delete(UUID proposalId) {
    var proposal = getOrThrow(proposalId);

    // Assumption is that permissions are already verified by the security filter chain
    proposalRepository.delete(proposal);
  }

  public ReadProposalDto setModuleTypes(UUID proposalId, Collection<String> moduleTypeKeys) {
    var proposal = getOrThrow(proposalId);
    var moduleTypes = this.moduleTypeRepository.findAllByKeyIn(moduleTypeKeys);
    // Assumption is that permissions are already verified by the security filter chain
    proposal.setModules(moduleTypes);
    proposal = proposalRepository.save(proposal);
    return proposalMapper.toDto(proposal);
  }

  public ReadProposalDto setSpecializations(
      UUID proposalId, Collection<String> specializationKeys) {
    var proposal = getOrThrow(proposalId);
    var specializations = this.specializationRepository.findAllByKeyIn(specializationKeys);
    // Assumption is that permissions are already verified by the security filter chain
    proposal.setSpecializations(specializations);
    proposal = proposalRepository.save(proposal);
    return proposalMapper.toDto(proposal);
  }

  @Transactional
  public ReadProjectDto promoteToProject(UUID proposalId, Authentication authentication) {
    var proposal = getOrThrow(proposalId);

    var principal = authentication.getPrincipal();
    if (!(principal instanceof Jwt)) throw new UnsupportedAuthenticationException();

    var jwt = (Jwt) principal;
    var name = jwt.getClaimAsString("name");
    if (name == null) throw new NoUsernameInAuthenticationException();

    var userId = UUID.fromString(authentication.getName());

    var projectRequest =
        this.promoteToProjectRequest(proposal, new CreateSupervisorDto(userId, name));
    var createdProject = this.projectService.create(projectRequest, proposal.getOwner());
    createdProject =
        this.projectService.setSpecializations(
            createdProject.id(),
            proposal.getSpecializations().stream().map(Specialization::getKey).toList());
    createdProject =
        this.projectService.setModuleTypes(
            createdProject.id(), proposal.getModules().stream().map(ModuleType::getKey).toList());
    this.delete(proposalId);

    return createdProject;
  }

  private Proposal getOrThrow(UUID proposalId) {
    return proposalRepository
        .findById(proposalId)
        .orElseThrow(() -> new ProposalNotFoundException(proposalId));
  }

  private CreateProjectDto promoteToProjectRequest(
      Proposal proposal, CreateSupervisorDto supervisor) {
    return new CreateProjectDto(
        proposal.getName(),
        null,
        proposal.getDescription(),
        proposal.getRequirement(),
        ProjectStatus.AVAILABLE,
        null,
        List.of(supervisor));
  }
}
