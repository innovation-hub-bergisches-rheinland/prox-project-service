package de.innovationhub.prox.projectservice.proposal;


import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import de.innovationhub.prox.projectservice.project.dto.CreateProjectDto;
import de.innovationhub.prox.projectservice.project.dto.CreateSupervisorDto;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import de.innovationhub.prox.projectservice.proposal.event.ProposalPromotedToProject;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ProposalService {

  private static final String PROPOSAL_TOPIC = "entity.proposal.proposal";
  private static final String PROPOSAL_PROMOTION_TOPIC = "event.proposal.promoted-to-project";
  private final ProposalRepository proposalRepository;
  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;
  private final ProposalMapper proposalMapper;
  private final ModuleTypeRepository moduleTypeRepository;
  private final SpecializationRepository specializationRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  public ProposalService(
    ProposalRepository proposalRepository,
    UserRepository userRepository,
    OrganizationRepository organizationRepository,
    ProposalMapper proposalMapper,
    ModuleTypeRepository moduleTypeRepository,
    SpecializationRepository specializationRepository,
    KafkaTemplate<String, Object> kafkaTemplate
  ) {
    this.proposalRepository = proposalRepository;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
    this.proposalMapper = proposalMapper;
    this.moduleTypeRepository = moduleTypeRepository;
    this.specializationRepository = specializationRepository;
    this.kafkaTemplate = kafkaTemplate;
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
        .orElseThrow();
    return create(proposalToCreate, org);
  }

  public ReadProposalDto createForUser(UUID userId, CreateProposalDto proposalToCreate) {
    // Assumption is that every user that enters the service is valid and already verified
    // by the security filter chain.

    // We know that when a user is authenticated, it also exists. So we should create it here.
    // Profile information will eventually be added.
    var user = this.userRepository.findById(userId)
      .orElseGet(() -> new User(userId, "UNKNWON"));
    return create(proposalToCreate, user);
  }

  private ReadProposalDto create(CreateProposalDto proposalToCreate, AbstractOwner owner) {
    var proposal = proposalMapper.toEntity(proposalToCreate);
    proposal.setOwner(owner);
    proposal = saveAndPublish(proposal);
    return proposalMapper.toDto(proposal);
  }

  public ReadProposalDto update(UUID proposalId, CreateProposalDto updatedProposal) {
    var proposal = getOrThrow(proposalId);

    // Assumption is that permissions are already verified by the security filter chain
    proposalMapper.updateProposal(proposal, updatedProposal);

    proposal = saveAndPublish(proposal);
    return proposalMapper.toDto(proposal);
  }

  public void delete(UUID proposalId) {
    var proposal = getOrThrow(proposalId);

    // Assumption is that permissions are already verified by the security filter chain
    proposalRepository.delete(proposal);
    this.kafkaTemplate.send(PROPOSAL_TOPIC, proposalId.toString(), null);
  }

  public ReadProposalDto setModuleTypes(UUID proposalId, Collection<String> moduleTypeKeys) {
    var proposal = getOrThrow(proposalId);
    var moduleTypes = this.moduleTypeRepository.findAllByKeyIn(moduleTypeKeys);
    // Assumption is that permissions are already verified by the security filter chain
    proposal.setModules(moduleTypes);
    proposal = saveAndPublish(proposal);
    return proposalMapper.toDto(proposal);
  }

  public ReadProposalDto setSpecializations(
      UUID proposalId, Collection<String> specializationKeys) {
    var proposal = getOrThrow(proposalId);
    var specializations = this.specializationRepository.findAllByKeyIn(specializationKeys);
    // Assumption is that permissions are already verified by the security filter chain
    proposal.setSpecializations(specializations);
    proposal = saveAndPublish(proposal);
    return proposalMapper.toDto(proposal);
  }

  @Transactional
  public ReadProposalDto applyCommitment(UUID proposalId, Authentication authentication) {
    var proposal = getOrThrow(proposalId);

    var principal = authentication.getPrincipal();
    if (!(principal instanceof Jwt)) throw new UnsupportedAuthenticationException();

    var jwt = (Jwt) principal;
    var name = jwt.getClaimAsString("name");
    if (name == null) throw new NoUsernameInAuthenticationException();

    var userId = UUID.fromString(authentication.getName());

    proposal.setStatus(ProposalStatus.HAS_COMMITMENT);
    proposal.setCommittedSupervisor(userId);

    this.saveAndPublish(proposal);

    this.kafkaTemplate.send(PROPOSAL_PROMOTION_TOPIC, proposalId.toString(),
      new ProposalPromotedToProject(proposalId, userId));

    return proposalMapper.toDto(proposal);
  }

  @Transactional
  public ReadProposalCollectionDto reconcile(List<UUID> projectIds) {
    var proposals = proposalRepository.findAllByIdIn(projectIds);
    // just resave is enough to publish the changes
    for (var proposal : proposals) {
      saveAndPublish(proposal);
    }
    return proposalMapper.toDto(proposals);
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

  private Proposal saveAndPublish(Proposal proposal) {
    proposal = proposalRepository.save(proposal);
    this.kafkaTemplate.send(PROPOSAL_TOPIC, proposal.getId().toString(), proposal);
    return proposal;
  }
}
