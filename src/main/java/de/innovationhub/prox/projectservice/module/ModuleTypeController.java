package de.innovationhub.prox.projectservice.module;

import de.innovationhub.prox.projectservice.module.dto.ReadModuleTypeCollectionDto;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModuleTypeController {
  private final ModuleTypeRepository moduleTypeRepository;

  public ModuleTypeController(ModuleTypeRepository moduleTypeRepository) {
    this.moduleTypeRepository = moduleTypeRepository;
  }

  @GetMapping("/moduleTypes")
  public @ResponseBody ResponseEntity<ReadModuleTypeCollectionDto> getAllModules() {
    var moduleTypes = this.moduleTypeRepository.findAll();
    return ResponseEntity.ok(ReadModuleTypeCollectionDto.fromModuleTypes(moduleTypes));
  }

  @GetMapping("/moduleTypes/search/findModulesOfSpecializations")
  public @ResponseBody ResponseEntity<ReadModuleTypeCollectionDto> findModulesOfSpecializations(
      @RequestParam("ids") Set<UUID> ids
  ) {
    var moduleTypes = this.moduleTypeRepository.findAllModuleTypesOfSpecializationId(ids);
    return ResponseEntity.ok(ReadModuleTypeCollectionDto.fromModuleTypes(moduleTypes));
  }
}
