package io.archilab.prox.projectservice.module;

import java.io.Serializable;
import java.net.URL;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExternalStudyCourseID implements Serializable {

  private static final long serialVersionUID = -1417466561604594013L;
  private String selfRef;


  public ExternalStudyCourseID(URL selfRef) {
    this.selfRef = selfRef.getFile();
  }

  @Override
  public String toString() {
    return this.selfRef;
  }
}
