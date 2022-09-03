package de.innovationhub.prox.projectservice.proposal;


import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProposalAdminController {
  private final ProposalService proposalService;

  @Autowired
  public ProposalAdminController(ProposalService proposalService) {
    this.proposalService = proposalService;
  }

  @PostMapping(value = "/proposals/reconciliation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalCollectionDto> reconcile(
    @RequestBody List<UUID> ids) {
    return ResponseEntity.ok(this.proposalService.reconcile(ids));
  }
}
