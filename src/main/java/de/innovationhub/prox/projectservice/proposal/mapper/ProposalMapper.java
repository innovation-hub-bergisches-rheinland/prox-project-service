package de.innovationhub.prox.projectservice.proposal.mapper;


import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ProposalPermissionsDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import de.innovationhub.prox.projectservice.security.OwnablePermissionEvaluatorHelper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProposalMapper {

  @Mapping(target = "permissions", expression = "java( this.getPermissions(proposal) )")
  public abstract ReadProposalDto toDto(Proposal proposal);

  @Autowired
  private OwnablePermissionEvaluatorHelper<Proposal> permissionEvaluatorHelper;

  public ProposalPermissionsDto getPermissions(Proposal proposal) {
    var hasPermission = permissionEvaluatorHelper.hasPermissionWithCurrentContext(proposal);
    return new ProposalPermissionsDto(hasPermission, hasPermission);
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
