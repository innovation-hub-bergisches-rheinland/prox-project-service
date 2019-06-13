package io.archilab.prox.projectservice.project;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupervisorName {

  private static final int MAX_LENGTH = 255;

  private String supervisorName;

  public SupervisorName(String supervisorName) {
    if (!SupervisorName.isValid(supervisorName)) {
      throw new IllegalArgumentException(
          String.format("Name %s exceeded maximum number of %d allowed characters", supervisorName,
              SupervisorName.MAX_LENGTH));
    }
    this.supervisorName = supervisorName;
  }

  public static boolean isValid(String name) {
    return name != null && name.length() <= SupervisorName.MAX_LENGTH;
  }
}
