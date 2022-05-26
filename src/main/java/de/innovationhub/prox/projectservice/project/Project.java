package de.innovationhub.prox.projectservice.project;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import de.innovationhub.prox.projectservice.core.AbstractEntity;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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

  @JsonProperty(access = Access.READ_ONLY)
  @Column(length = 255)
  private String creatorName;

  @Column(length = 255)
  @Size(max = 255)
  private String supervisorName;

  @JsonProperty(access = Access.READ_ONLY)
  @ManyToMany
  private Set<Specialization> specializations = new HashSet<>();

  @JsonIgnore @ManyToMany private Set<ModuleType> modules = new HashSet<>();

  @JsonProperty(access = Access.READ_ONLY)
  @ManyToOne(optional = false)
  @JoinColumn(name = "owner_id", nullable = false, updatable = false)
  private AbstractOwner owner;

  @JsonProperty(access = Access.READ_ONLY)
  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @JsonProperty(access = Access.READ_ONLY)
  @Column(name = "modified_at")
  @UpdateTimestamp
  private Instant modifiedAt;
}
