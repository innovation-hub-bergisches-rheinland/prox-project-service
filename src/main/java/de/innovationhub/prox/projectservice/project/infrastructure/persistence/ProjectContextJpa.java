package de.innovationhub.prox.projectservice.project.infrastructure.persistence;

import de.innovationhub.prox.projectservice.project.domain.ContextDefinition;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProjectContextJpa {
  @Enumerated
  @Column(name = "context", nullable = false)
  private ContextDefinition context;

  @Column(name = "context_id", nullable = false)
  private UUID contextId;

  @Column(name = "context_name")
  private String contextName;
}
