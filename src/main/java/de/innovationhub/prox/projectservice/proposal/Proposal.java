package de.innovationhub.prox.projectservice.proposal;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import de.innovationhub.prox.projectservice.core.AbstractEntity;
import de.innovationhub.prox.projectservice.core.Ownable;
import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
public class Proposal extends AbstractEntity {
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

  @ManyToMany(fetch = FetchType.EAGER)
  @Builder.Default
  private Set<Specialization> specializations = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @Builder.Default
  private Set<ModuleType> modules = new HashSet<>();

  @Column(name = "owner_id", nullable = false, updatable = false)
  private UUID ownerId;

  private UUID committedSupervisor;

  // Back-reference to the project if one has been generated from the proposal.
  // Note: This is a pragmatic workaround so that we can navigate to the created proposal, once it
  //  has been created. There might be a better approach.
  private UUID projectId;

  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> tags = new HashSet<>();

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

  public void setTags(Collection<String> tags) {
    this.tags = new HashSet<>(tags);
  }
}
