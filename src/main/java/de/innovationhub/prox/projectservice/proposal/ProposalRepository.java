package de.innovationhub.prox.projectservice.proposal;


import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends PagingAndSortingRepository<Proposal, UUID> {
}
