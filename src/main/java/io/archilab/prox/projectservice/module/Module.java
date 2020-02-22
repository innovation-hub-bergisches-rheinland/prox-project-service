package io.archilab.prox.projectservice.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.prox.projectservice.core.AbstractEntity;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Module extends AbstractEntity {

  @JsonIgnore private ExternalModuleID externalModuleID;

  @JsonUnwrapped private ModuleName name;

  private ProjectType projectType;

  @ManyToOne private StudyCourse studyCourse;

  public Module(ModuleName name, ProjectType projectType) {
    this.name = name;
    this.projectType = projectType;
  }
}
