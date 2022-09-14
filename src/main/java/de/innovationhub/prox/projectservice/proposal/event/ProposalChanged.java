package de.innovationhub.prox.projectservice.proposal.event;

import de.innovationhub.prox.projectservice.core.event.Event;
import de.innovationhub.prox.projectservice.proposal.Proposal;

/**
 * This event is being fired when the Proposal entity changes in any way
 * so we can eventually publish the entity state
 * @param proposal the CHANGED proposal
 */
public record ProposalChanged(
  Proposal proposal
) implements Event {

}
