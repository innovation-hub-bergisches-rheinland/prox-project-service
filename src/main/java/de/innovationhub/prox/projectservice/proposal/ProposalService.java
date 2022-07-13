package de.innovationhub.prox.projectservice.proposal;

import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.organization.Organization;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import de.innovationhub.prox.projectservice.proposal.exception.ProposalNotFoundException;
import de.innovationhub.prox.projectservice.proposal.mapper.ProposalMapper;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class ProposalService {
  private final ProposalRepository proposalRepository;
  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;
  private final ProposalMapper proposalMapper;
  private final ModuleTypeRepository moduleTypeRepository;
  private final SpecializationRepository specializationRepository;

  public ProposalService(ProposalRepository proposalRepository, UserRepository userRepository,
      OrganizationRepository organizationRepository, ProposalMapper proposalMapper,
      ModuleTypeRepository moduleTypeRepository, SpecializationRepository specializationRepository) {
    this.proposalRepository = proposalRepository;
    this.userRepository = userRepository;
    this.organizationRepository = organizationRepository;
    this.proposalMapper = proposalMapper;
    this.moduleTypeRepository = moduleTypeRepository;
    this.specializationRepository = specializationRepository;
  }

  public ReadProposalCollectionDto getAll() {
    var all = proposalRepository.findAll();
    return proposalMapper.toDto(StreamSupport.stream(all.spliterator(), false).toList());
  }

  public ReadProposalDto get(UUID proposalId) {
    var proposal = getOrThrow(proposalId);

    return proposalMapper.toDto(proposal);
  }

  public ReadProposalDto createForOrganization(UUID organizationId, CreateProposalDto proposalToCreate) {
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

  public ReadProposalDto setSpecializations(UUID proposalId, Collection<String> specializationKeys) {
    var proposal = getOrThrow(proposalId);
    var specializations = this.specializationRepository.findAllByKeyIn(specializationKeys);
    // Assumption is that permissions are already verified by the security filter chain
    proposal.setSpecializations(specializations);
    proposal = proposalRepository.save(proposal);
    return proposalMapper.toDto(proposal);
  }

  private Proposal getOrThrow(UUID proposalId) {
    return proposalRepository.findById(proposalId).orElseThrow(() -> new ProposalNotFoundException(proposalId));
  }
}
