package de.innovationhub.prox.projectservice.proposal.dto;


import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.proposal.ProposalStatus;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ReadProposalDto(
    UUID id,
    String name,
    String description,
    String requirement,
    ProposalStatus status,
    Set<Specialization> specializations,
    Set<ModuleType> modules,
    AbstractOwner owner,
    Instant createdAt,
    Instant modifiedAt
) {}
