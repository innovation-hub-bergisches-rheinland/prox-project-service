package io.archilab.prox.projectservice.project;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.prox.projectservice.core.AbstractEntity;
import io.archilab.prox.projectservice.module.Module;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends AbstractEntity {

  @Setter @JsonUnwrapped private ProjectName name;

  @Setter @JsonUnwrapped private ProjectDescription description;

  @Setter @JsonUnwrapped private ProjectShortDescription shortDescription;

  @Setter @JsonUnwrapped private ProjectRequirement requirement;

  @Setter private ProjectStatus status;

  @NotNull @Setter @JsonUnwrapped private CreatorID creatorID;

  @NotNull @Setter @JsonUnwrapped private CreatorName creatorName;

  @NotNull @Setter @JsonUnwrapped private SupervisorName supervisorName;

  @Getter @ManyToMany private List<Module> modules = new ArrayList<>();

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Column(updatable = false)
  @CreationTimestamp
  private java.util.Date created;

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @UpdateTimestamp
  private java.util.Date modified;

  public Project(
      ProjectName name,
      ProjectShortDescription shortDescription,
      ProjectDescription description,
      ProjectStatus status,
      ProjectRequirement requirement,
      @NotNull CreatorID creatorID,
      @NotNull CreatorName creatorName,
      @NotNull SupervisorName supervisorName,
      List<Module> modules) {
    this.requirement = requirement;
    this.name = name;
    this.shortDescription = shortDescription;
    this.description = description;
    this.status = status;
    this.creatorID = creatorID;
    this.creatorName = creatorName;
    this.supervisorName = supervisorName;
    this.modules = modules;
  }

  public Project(
      ProjectName name,
      ProjectShortDescription shortDescription,
      ProjectDescription description,
      ProjectStatus status,
      ProjectRequirement requirement,
      @NotNull CreatorID creatorID,
      @NotNull CreatorName creatorName,
      @NotNull SupervisorName supervisorName) {
    this.requirement = requirement;
    this.name = name;
    this.shortDescription = shortDescription;
    this.description = description;
    this.status = status;
    this.creatorID = creatorID;
    this.creatorName = creatorName;
    this.supervisorName = supervisorName;
  }
}
