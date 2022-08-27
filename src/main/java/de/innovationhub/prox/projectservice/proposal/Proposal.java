package de.innovationhub.prox.projectservice.proposal;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import de.innovationhub.prox.projectservice.core.AbstractEntity;
import de.innovationhub.prox.projectservice.core.Ownable;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

/**
 * A proposal (also known as "idea") is a pre-state of a project where a user has an idea for a
 * project but not the capacity, resources, knowledge or the rights to supervise it.
 *
 * <p>The intention behind a proposal is to draft potentially immature project ideas in a
 * lightweight format. The key difference between a project and a proposal is, that a proposal does
 * not have a supervisor yet.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class Proposal extends AbstractEntity implements Ownable {
  @Column(length = 255)
  @Size(min = 1, max = 255)
  @NotBlank
  @NotNull
  private String name;

  @Column(length = 10000)
  @Size(min = 1, max = 10000)
  @NotBlank
  private String description;

  @Column(length = 10000)
  private String requirement;

  @NotNull
  @Enumerated(EnumType.ORDINAL)
  @Builder.Default
  private ProposalStatus status = ProposalStatus.PROPOSED;

  @JsonProperty(access = Access.READ_ONLY)
  @ManyToMany
  @Builder.Default
  private Set<Specialization> specializations = new HashSet<>();

  @JsonIgnore @ManyToMany @Builder.Default private Set<ModuleType> modules = new HashSet<>();

  @JsonProperty(access = Access.READ_ONLY)
  @ManyToOne(optional = false)
  @JoinColumn(name = "owner_id", nullable = false, updatable = false)
  private AbstractOwner owner;

  @JsonProperty(access = Access.READ_ONLY)
  @Column(name = "status_changed_at")
  @Builder.Default
  private Instant statusChangedAt = Instant.now();

  @JsonProperty(access = Access.READ_ONLY)
  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  @Builder.Default
  private Instant createdAt = Instant.now();

  @JsonProperty(access = Access.READ_ONLY)
  @Column(name = "modified_at")
  @UpdateTimestamp
  @Builder.Default
  private Instant modifiedAt = Instant.now();

  public void setStatus(ProposalStatus status) {
    this.status = status;
    statusChangedAt = Instant.now();
  }
}
