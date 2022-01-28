package de.innovationhub.prox.projectservice.project.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectContext {
  private final ContextDefinition contextDefinition;
  private final ContextId id;
  private String contextName;

  public ProjectContext(
      ContextDefinition contextDefinition,
      ContextId id) {
    this.contextDefinition = contextDefinition;
    this.id = id;
  }
}
