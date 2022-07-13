package de.innovationhub.prox.projectservice.proposal;


import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends PagingAndSortingRepository<Proposal, UUID> {
  // TODO: We might cause troubles by using the modifiedAt timestamp.
  //  maybe it would be useful to integrate a new timestamp that is capable of tracking a status
  //  update
  @Query("select p from Proposal p where p.status = ?1 and p.modifiedAt <= ?2")
  List<Proposal> findWithStatusModifiedBefore(ProposalStatus status, Instant timestamp);
}
