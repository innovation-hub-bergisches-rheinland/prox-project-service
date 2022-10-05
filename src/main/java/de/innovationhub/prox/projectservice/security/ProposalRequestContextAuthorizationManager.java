package de.innovationhub.prox.projectservice.security;


import de.innovationhub.prox.projectservice.proposal.Proposal;
import de.innovationhub.prox.projectservice.proposal.ProposalRepository;
import java.util.UUID;
import java.util.function.Supplier;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProposalRequestContextAuthorizationManager
    implements AuthorizationManager<RequestAuthorizationContext> {
  public static final String PROPOSAL_ID_VARIABLE = "proposalId";
  private final ProposalRepository proposalRepository;
  private final OwnablePermissionEvaluatorHelper<Proposal> permissionEvaluatorHelper;

  public ProposalRequestContextAuthorizationManager(
      ProposalRepository proposalRepository,
      OwnablePermissionEvaluatorHelper<Proposal> permissionEvaluatorHelper) {
    this.proposalRepository = proposalRepository;
    this.permissionEvaluatorHelper = permissionEvaluatorHelper;
  }

  @Override
  @Transactional
  public AuthorizationDecision check(
      Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    // If we don't have a projectId in the request context, we can't do anything
    var value = object.getVariables().get(PROPOSAL_ID_VARIABLE);
    if (value == null || value.isBlank()) {
      log.warn(
          "No authorization decision could be made because the request variable '{}' is not present",
          PROPOSAL_ID_VARIABLE);
      return null;
    }

    var auth = authentication.get();
    if (!auth.isAuthenticated()) {
      return new AuthorizationDecision(false);
    }

    var optProposal = proposalRepository.findById(UUID.fromString(value));
    if (optProposal.isEmpty()) {
      // Can't decide if a project doesn't exist
      return null;
    }

    var proposal = optProposal.get();

    return new AuthorizationDecision(
      permissionEvaluatorHelper.hasPermission(proposal, auth));
  }
}
