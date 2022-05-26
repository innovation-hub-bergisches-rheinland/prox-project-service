package de.innovationhub.prox.projectservice.module;

import de.innovationhub.prox.projectservice.module.dto.ReadModuleTypeCollectionDto;
import de.innovationhub.prox.projectservice.module.dto.ReadSpecializationCollectionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpecializationController {
  private final SpecializationRepository specializationRepository;

  public SpecializationController(SpecializationRepository specializationRepository) {
    this.specializationRepository = specializationRepository;
  }

  @GetMapping("/specializations")
  public @ResponseBody ResponseEntity<ReadSpecializationCollectionDto> getAllSpecializations() {
    var specializations = this.specializationRepository.findAll();
    return ResponseEntity.ok(ReadSpecializationCollectionDto.fromSpecializations(specializations));
  }
}
