package de.innovationhub.prox.projectservice.proposal;


import de.innovationhub.prox.projectservice.core.event.EventPublisher;
import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import de.innovationhub.prox.projectservice.proposal.event.ProposalChanged;
import de.innovationhub.prox.projectservice.proposal.event.ProposalDeleted;
import de.innovationhub.prox.projectservice.proposal.event.ProposalReceivedCommitment;
import de.innovationhub.prox.projectservice.proposal.exception.NoUsernameInAuthenticationException;
import de.innovationhub.prox.projectservice.proposal.exception.ProposalNotFoundException;
import de.innovationhub.prox.projectservice.proposal.exception.UnsupportedAuthenticationException;
import de.innovationhub.prox.projectservice.proposal.mapper.ProposalMapper;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
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
  private final EventPublisher eventPublisher;

  @Autowired
  public ProposalService(
    ProposalRepository proposalRepository,
    UserRepository userRepository,
    OrganizationRepository organizationRepository,
    ProposalMapper proposalMapper,
    ModuleTypeRepository moduleTypeRepository,
    SpecializationRepository specializationRepository,
    EventPublisher eventPublisher) {
    this.proposalRepository = proposalRepository;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
    this.proposalMapper = proposalMapper;
    this.moduleTypeRepository = moduleTypeRepository;
    this.specializationRepository = specializationRepository;
    this.eventPublisher = eventPublisher;
  }

  public ReadProposalCollectionDto getAll(ProposalStatus status) {
    var all = proposalRepository.findAllByStatus(status);
    return proposalMapper.toDto(StreamSupport.stream(all.spliterator(), false).toList());
  }

  public ReadProposalDto get(UUID proposalId) {
    var proposal = getOrThrow(proposalId);

    return proposalMapper.toDto(proposal);
  }

  @Transactional(TxType.REQUIRED)
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

  @Transactional(TxType.REQUIRED)
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
    proposal.setOwnerId(owner.getId());
    proposal = saveAndPublish(proposal);
    return proposalMapper.toDto(proposal);
  }

  @Transactional(TxType.REQUIRED)
  public ReadProposalDto update(UUID proposalId, CreateProposalDto updatedProposal) {
    var proposal = getOrThrow(proposalId);

    // Assumption is that permissions are already verified by the security filter chain
    proposalMapper.updateProposal(proposal, updatedProposal);

    proposal = saveAndPublish(proposal);
    return proposalMapper.toDto(proposal);
  }

  @Transactional(TxType.REQUIRED)
  public void delete(UUID proposalId) {
    var proposal = getOrThrow(proposalId);

    // Assumption is that permissions are already verified by the security filter chain
    proposalRepository.delete(proposal);
    this.eventPublisher.publish(new ProposalDeleted(proposalId));
  }

  @Transactional(TxType.REQUIRED)
  public ReadProposalDto setModuleTypes(UUID proposalId, Collection<String> moduleTypeKeys) {
    var proposal = getOrThrow(proposalId);
    var moduleTypes = this.moduleTypeRepository.findAllByKeyIn(moduleTypeKeys);
    // Assumption is that permissions are already verified by the security filter chain
    proposal.setModules(moduleTypes);
    proposal = saveAndPublish(proposal);
    return proposalMapper.toDto(proposal);
  }

  @Transactional(TxType.REQUIRED)
  public ReadProposalDto setSpecializations(
      UUID proposalId, Collection<String> specializationKeys) {
    var proposal = getOrThrow(proposalId);
    var specializations = this.specializationRepository.findAllByKeyIn(specializationKeys);
    // Assumption is that permissions are already verified by the security filter chain
    proposal.setSpecializations(specializations);
    proposal = saveAndPublish(proposal);
    return proposalMapper.toDto(proposal);
  }

  @Transactional(TxType.REQUIRED)
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

    var event = new ProposalReceivedCommitment(proposalId, userId);

    this.eventPublisher.publish(event);

    proposal = getOrThrow(proposalId);
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

  private Proposal saveAndPublish(Proposal proposal) {
    proposal = proposalRepository.save(proposal);
    this.eventPublisher.publish(new ProposalChanged(proposal));
    return proposal;
  }
}
