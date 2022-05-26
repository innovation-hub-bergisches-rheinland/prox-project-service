package de.innovationhub.prox.projectservice.module.dto;

import de.innovationhub.prox.projectservice.module.ModuleType;
import java.util.List;
import java.util.stream.StreamSupport;

public record ReadModuleTypeCollectionDto(
    List<ReadModuleTypeDto> modules
) {
    public static ReadModuleTypeCollectionDto fromModuleTypes(Iterable<ModuleType> modules) {
        return new ReadModuleTypeCollectionDto(StreamSupport.stream(modules.spliterator(), false).map(
            ReadModuleTypeDto::fromModuleType).toList());
    }
}
