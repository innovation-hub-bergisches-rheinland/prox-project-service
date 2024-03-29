package de.innovationhub.prox.projectservice.proposal;


import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProposalController {
  private final ProposalService proposalService;

  @Autowired
  public ProposalController(ProposalService proposalService) {
    this.proposalService = proposalService;
  }

  /*
   * ----------------------------
   * CRUD
   * ----------------------------
   */

  @GetMapping(value = "/proposals", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalCollectionDto> getAll(
    @RequestParam(name = "status", defaultValue = "PROPOSED") ProposalStatus status
  ) {
    var proposals = this.proposalService.getAll(status);
    return ResponseEntity.ok(proposals);
  }

  @GetMapping(value = "/proposals/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> getById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(this.proposalService.get(id));
  }

  @Transactional
  @PutMapping(
      value = "/proposals/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> updateById(
      @PathVariable("id") UUID id, @RequestBody CreateProposalDto proposalDto) {
    var updated = this.proposalService.update(id, proposalDto);

    return ResponseEntity.ok(updated);
  }

  @Transactional
  @PutMapping(
      value = "/proposals/{id}/specializations",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> setSpecializationsById(
      @PathVariable("id") UUID id, @RequestBody Set<String> specializationKeys) {
    var updated = this.proposalService.setSpecializations(id, specializationKeys);
    return ResponseEntity.ok(updated);
  }

  @Transactional
  @PutMapping(
      value = "/proposals/{id}/modules",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> setModuleTypesById(
      @PathVariable("id") UUID id, @RequestBody Set<String> moduleTypeKeys) {
    var updated = this.proposalService.setModuleTypes(id, moduleTypeKeys);
    return ResponseEntity.ok(updated);
  }

  @Transactional
  @DeleteMapping(value = "/proposals/{id}")
  public @ResponseBody ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
    this.proposalService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Transactional
  @PreAuthorize("hasRole('professor')")
  @PostMapping(value = "/proposals/{id}/commitment")
  public @ResponseBody ResponseEntity<ReadProposalDto> commitForProposal(
      @PathVariable("id") UUID id, Authentication auth) {
    var proposal = this.proposalService.applyCommitment(id, auth);
    return ResponseEntity.status(HttpStatus.OK).body(proposal);
  }

  /*
   * ----------------------------
   * Contexts
   * ----------------------------
   */
  @Transactional
  @PostMapping(
      value = "/users/{id}/proposals",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> createForUser(
      @PathVariable("id") UUID userId, @RequestBody CreateProposalDto proposal) {
    var created = this.proposalService.createForUser(userId, proposal);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @Transactional
  @PostMapping(
      value = "/organizations/{id}/proposals",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> createOrganizationProject(
      @PathVariable("id") UUID orgId, @RequestBody CreateProposalDto proposal) {
    var created = this.proposalService.createForOrganization(orgId, proposal);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
}
