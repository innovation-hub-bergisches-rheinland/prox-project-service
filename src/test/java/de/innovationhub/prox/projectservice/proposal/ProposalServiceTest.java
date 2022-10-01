package de.innovationhub.prox.projectservice.proposal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.organization.OrganizationRepository;
import de.innovationhub.prox.projectservice.owners.user.UserRepository;
import de.innovationhub.prox.projectservice.proposal.dto.CreateProposalDto;
import de.innovationhub.prox.projectservice.proposal.exception.ProposalNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ProposalServiceTest {

  @MockBean
  ProposalRepository proposalRepository;

  @MockBean
  UserRepository userRepository;

  @MockBean
  OrganizationRepository organizationRepository;

  @MockBean
  ModuleTypeRepository moduleTypeRepository;

  @MockBean
  SpecializationRepository specializationRepository;

  @Autowired
  ProposalService proposalService;

  @Test
  void shouldThrowWhenProposalNotFound() {
    when(proposalRepository.findById(any())).thenReturn(Optional.empty());

    var id = UUID.randomUUID();

    assertThrows(ProposalNotFoundException.class, () -> proposalService.get(id));
    assertThrows(ProposalNotFoundException.class, () -> proposalService.delete(id));
    assertThrows(
        ProposalNotFoundException.class,
        () -> proposalService.update(id, new CreateProposalDto("", "", "")));
    assertThrows(
        ProposalNotFoundException.class,
        () -> proposalService.setModuleTypes(id, Collections.emptySet()));
    assertThrows(
        ProposalNotFoundException.class,
        () -> proposalService.setSpecializations(id, Collections.emptySet()));
  }
}
