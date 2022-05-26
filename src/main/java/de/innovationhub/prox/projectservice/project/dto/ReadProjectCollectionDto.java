package de.innovationhub.prox.projectservice.project.dto;

import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.xml.transform.stream.StreamSource;

public record ReadProjectCollectionDto(
    List<ReadProjectDto> projects
) {
    public static ReadProjectCollectionDto fromProjects(Iterable<Project> projects) {
        return new ReadProjectCollectionDto(StreamSupport.stream(projects.spliterator(), false).map(ReadProjectDto::fromProject).toList());
    }
}
