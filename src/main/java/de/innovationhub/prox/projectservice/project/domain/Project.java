package de.innovationhub.prox.projectservice.project.domain;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Project {
  @Builder.Default
  private final ProjectId projectId = new ProjectId(UUID.randomUUID());
  private String name;
  private String description;
  private String shortDescription;
  private String requirements;
  private String supervisorName;
  private ProjectStatus status;
  private ProjectContext context;

  public record ProjectId(UUID id) {}
}
