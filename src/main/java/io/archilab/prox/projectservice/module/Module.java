package io.archilab.prox.projectservice.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.prox.projectservice.core.AbstractEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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

  private ProjectType projectType;
  
//  @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.PERSIST })
//  @JoinColumn(name = "studyCourse_id", nullable = true) 
  @ManyToOne
  private StudyCourse studyCourse;




  public Module(ModuleName name, ProjectType projectType) {
    this.name = name;
    this.projectType = projectType;
  }
  

}
