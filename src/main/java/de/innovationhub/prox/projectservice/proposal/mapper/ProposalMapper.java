package de.innovationhub.prox.projectservice.proposal.mapper;


import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.owners.AbstractOwnerRepository;
import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ProposalPermissionsDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import de.innovationhub.prox.projectservice.security.OwnerPermissionEvaluatorHelper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Mapper(componentModel = "spring")
public abstract class ProposalMapper {
  public static final ProposalMapper INSTANCE = Mappers.getMapper(ProposalMapper.class);

  @Mapping(target = "permissions", expression = "java( this.getPermissions(proposal) )")
  @Mapping(target = "owner", expression = "java( this.getOwner(proposal.getOwnerId()) )")
  public abstract ReadProposalDto toDto(Proposal proposal);

  @Autowired
  private OwnerPermissionEvaluatorHelper permissionEvaluatorHelper;

  @Autowired private AbstractOwnerRepository abstractOwnerRepository;

  public ProposalPermissionsDto getPermissions(Proposal proposal) {
    var hasPermission = permissionEvaluatorHelper.hasPermissionWithCurrentContext(proposal.getOwnerId());
    return new ProposalPermissionsDto(hasPermission, hasPermission, hasRole("ROLE_professor"));
  }

  public AbstractOwner getOwner(UUID ownerId) {
    return abstractOwnerRepository.findById(ownerId).orElse(null);
  }

  // TODO: Not mapper logic
  private boolean hasRole(String role) {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    if (authentication == null) {
      return false;
    }

    for (GrantedAuthority auth : authentication.getAuthorities()) {
      if (role.equals(auth.getAuthority())) {
        return true;
      }
    }

    return false;
  }

  public ReadProposalCollectionDto toDto(List<Proposal> proposals) {
    return new ReadProposalCollectionDto(proposals.stream().map(this::toDto).toList());
  }

  public abstract Proposal toEntity(CreateProposalDto proposalDto);

  public void updateProposal(Proposal proposalToUpdate, CreateProposalDto updated) {
    if (updated == null) {
      return;
    }

    proposalToUpdate.setName(updated.name());
    proposalToUpdate.setDescription(updated.description());
    proposalToUpdate.setRequirement(updated.requirement());
  }
}
