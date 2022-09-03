package de.innovationhub.prox.projectservice.proposal;


import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends PagingAndSortingRepository<Proposal, UUID> {

  @Query("select p from Proposal p where p.status = ?1 and p.statusChangedAt <= ?2")
  List<Proposal> findWithStatusModifiedBefore(ProposalStatus status, Instant timestamp);

  @Query("select p from Proposal p where p.id in ?1 order by p.modifiedAt desc")
  List<Proposal> findAllByIdIn(Collection<UUID> ids);
}
