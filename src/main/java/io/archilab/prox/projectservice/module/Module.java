package io.archilab.prox.projectservice.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.prox.projectservice.core.AbstractEntity;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Module extends AbstractEntity {

  @JsonIgnore
  private ExternalModuleID externalModuleID;

  @JsonUnwrapped
  private ModuleName name;


  public Module(ModuleName name) {
    this.name = name;
  }
}
