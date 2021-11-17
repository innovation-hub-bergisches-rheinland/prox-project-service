package de.innovationhub.prox.projectservice.project;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

  @Column(length = MAX_LENGTH)
  @Size(max = MAX_LENGTH)
  private String supervisorName;

  public SupervisorName(String supervisorName) {
    this.supervisorName = supervisorName;
  }
}
