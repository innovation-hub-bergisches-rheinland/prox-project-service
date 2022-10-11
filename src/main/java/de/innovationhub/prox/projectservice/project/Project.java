package de.innovationhub.prox.projectservice.project;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import de.innovationhub.prox.projectservice.core.AbstractEntity;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class Project extends AbstractEntity {

  @Column(length = 255)
  @Size(min = 1, max = 255)
  @NotBlank
  @NotNull
  private String name;

  @Column(length = 10000)
  private String description;

  @Column(length = 10000)
  @Size(min = 1, max = 10000)
  @NotBlank
  private String shortDescription;

  @Column(length = 10000)
  private String requirement;

  @NotNull private ProjectStatus status;

  @Column(length = 255)
  private String creatorName;

  @ElementCollection
  @CollectionTable(uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "id"})})
  @Builder.Default
  private List<Supervisor> supervisors = new ArrayList<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @Builder.Default
  private Set<Specialization> specializations = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @Builder.Default
  private Set<ModuleType> modules = new HashSet<>();

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  // If the project has been created from a proposal, we store the corresponding proposal id here.
  private UUID proposalId = null;

  @ElementCollection(fetch = FetchType.EAGER)
  private List<String> tags = new ArrayList<>();

  @JsonProperty(access = Access.READ_ONLY)
  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @JsonProperty(access = Access.READ_ONLY)
  @Column(name = "modified_at")
  @UpdateTimestamp
  private Instant modifiedAt;

  public boolean isSupervisor(UUID id) {
    return supervisors.stream().anyMatch(s -> s.getId().equals(id));
  }
}
