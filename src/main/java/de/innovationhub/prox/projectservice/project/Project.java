/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.projectservice.project;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.innovationhub.prox.projectservice.core.AbstractEntity;
import de.innovationhub.prox.projectservice.module.ModuleType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
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
@Setter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends AbstractEntity {

  @NotNull
  @Valid
  @JsonUnwrapped
  private ProjectName name;

  @JsonUnwrapped
  @Valid
  private ProjectDescription description;

  @JsonUnwrapped
  @Valid
  @NotNull
  private ProjectShortDescription shortDescription;

  @JsonUnwrapped
  @Valid
  private ProjectRequirement requirement;

  @JsonUnwrapped
  @Valid
  @NotNull
  private ProjectStatus status;

  @Column(updatable = false)
  @JsonUnwrapped
  @Valid
  @NotNull
  private ProjectContext context;

  @JsonUnwrapped
  @Valid
  @NotNull
  private CreatorID creatorID;

  @JsonUnwrapped
  @Valid
  @NotNull
  private CreatorName creatorName;

  @JsonUnwrapped
  @Valid
  private SupervisorName supervisorName;

  @ManyToMany
  private Set<ModuleType> modules = new HashSet<>();

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
      CreatorID creatorID,
      CreatorName creatorName,
      SupervisorName supervisorName,
      Set<ModuleType> modules,
      ProjectContext projectContext) {
    this.requirement = requirement;
    this.name = name;
    this.shortDescription = shortDescription;
    this.description = description;
    this.status = status;
    this.creatorID = creatorID;
    this.creatorName = creatorName;
    this.supervisorName = supervisorName;
    this.modules = modules;
    this.context = projectContext;
  }

  public Project(
      ProjectName name,
      ProjectShortDescription shortDescription,
      ProjectDescription description,
      ProjectStatus status,
      ProjectRequirement requirement,
      CreatorID creatorID,
      CreatorName creatorName,
      SupervisorName supervisorName,
      ProjectContext projectContext) {
    this.requirement = requirement;
    this.name = name;
    this.shortDescription = shortDescription;
    this.description = description;
    this.status = status;
    this.creatorID = creatorID;
    this.creatorName = creatorName;
    this.supervisorName = supervisorName;
    this.context = projectContext;
  }
}
