package de.innovationhub.prox.projectservice.proposal;


import de.innovationhub.prox.projectservice.project.dto.ReadProjectDto;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalCollectionDto;
import de.innovationhub.prox.projectservice.proposal.dto.ReadProposalDto;
import java.util.Set;
import java.util.UUID;
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
  public @ResponseBody ResponseEntity<ReadProposalCollectionDto> getAll() {
    var proposals = this.proposalService.getAll();
    return ResponseEntity.ok(proposals);
  }

  @GetMapping(value = "/proposals/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> getById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(this.proposalService.get(id));
  }

  @PutMapping(
      value = "/proposals/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> updateById(
      @PathVariable("id") UUID id, @RequestBody CreateProposalDto proposalDto) {
    var updated = this.proposalService.update(id, proposalDto);

    return ResponseEntity.ok(updated);
  }

  @PutMapping(
      value = "/proposals/{id}/specializations",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> setSpecializationsById(
      @PathVariable("id") UUID id, @RequestBody Set<String> specializationKeys) {
    var updated = this.proposalService.setSpecializations(id, specializationKeys);
    return ResponseEntity.ok(updated);
  }

  @PutMapping(
      value = "/proposals/{id}/modules",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> setModuleTypesById(
      @PathVariable("id") UUID id, @RequestBody Set<String> moduleTypeKeys) {
    var updated = this.proposalService.setModuleTypes(id, moduleTypeKeys);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping(value = "/proposals/{id}")
  public @ResponseBody ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
    this.proposalService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PreAuthorize("hasRole('professor')")
  @PostMapping(value = "/proposals/{id}/commitment")
  public @ResponseBody ResponseEntity<ReadProjectDto> commitForProposal(@PathVariable("id") UUID id, Authentication auth) {
    var createdProject = this.proposalService.promoteToProject(id, auth);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
  }

  /*
   * ----------------------------
   * Contexts
   * ----------------------------
   */
  @PostMapping(
      value = "/users/{id}/proposals",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody ResponseEntity<ReadProposalDto> createForUser(
      @PathVariable("id") UUID userId, @RequestBody CreateProposalDto proposal) {
    var created = this.proposalService.createForUser(userId, proposal);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

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
