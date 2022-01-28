package de.innovationhub.prox.projectservice.project.infrastructure.persistence;

import de.innovationhub.prox.projectservice.project.domain.ContextDefinition;
import de.innovationhub.prox.projectservice.project.domain.ProjectStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners({AuditingEntityListener.class})
@Table(name = "projects")
public class ProjectJpaEntity {
  @Id
  @Column(name = "project_id", unique = true, updatable = false)
  private UUID projectId;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "description", length = 10000)
  private String description;

  @Column(name = "short_description", length = 10000)
  private String shortDescription;

  @Column(name = "requirements", length = 10000)
  private String requirement;

  @Column(name = "status")
  private ProjectStatus status;

  @Embedded
  private ProjectContextJpa context;

  @Column(name = "supervisor_name")
  private String supervisorName;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime created;

  @Column(name = "modified_at")
  @UpdateTimestamp
  private LocalDateTime modified;
}
