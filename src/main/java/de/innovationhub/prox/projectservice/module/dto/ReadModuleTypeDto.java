package de.innovationhub.prox.projectservice.module.dto;


import de.innovationhub.prox.projectservice.module.ModuleType;
import java.util.UUID;

public record ReadModuleTypeDto(UUID id, String name, String key) {
  public static ReadModuleTypeDto fromModuleType(ModuleType moduleType) {
    return new ReadModuleTypeDto(moduleType.getId(), moduleType.getName(), moduleType.getKey());
  }
}
