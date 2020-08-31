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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExternalModuleID implements Serializable {

  private static final long serialVersionUID = 884259381838692316L;
  private String selfRef;


  public ExternalModuleID(URL selfRef) {
    this.selfRef = selfRef.getFile();
  }

  @Override
  public String toString() {
    return this.selfRef;
  }
}
