package de.innovationhub.prox.projectservice.proposal.mapper;


import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ProposalMapper {
  public abstract ReadProposalDto toDto(Proposal proposal);

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
