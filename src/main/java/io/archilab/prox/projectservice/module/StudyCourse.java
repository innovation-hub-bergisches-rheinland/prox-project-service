package io.archilab.prox.projectservice.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.archilab.prox.projectservice.core.AbstractEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCourse extends AbstractEntity {

  @Setter
  @JsonIgnore
  private ExternalStudyCourseID externalStudyCourseID;

  @Setter
  @JsonUnwrapped
  private StudyCourseName name;

  @Setter
  private AcademicDegree academicDegree;

  @OneToMany
  private List<Module> modules = new ArrayList<>();


  public StudyCourse(StudyCourseName name, AcademicDegree academicDegree) {
    this.name = name;
    this.academicDegree = academicDegree;
  }

  public List<Module> getModules() {
    return Collections.unmodifiableList(this.modules);
  }

  public void addModule(Module module) {
    this.modules.add(module);
  }

  public void removeModule(Module module) {
    this.modules.remove(module);
  }
}
