package de.innovationhub.prox.projectservice.proposal.event;

import de.innovationhub.prox.projectservice.core.event.Event;
import java.util.UUID;

public record ProposalDeleted(
  UUID proposalId
) implements Event {

}
