package de.innovationhub.prox.projectservice.project;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectRequirement {
  private static final int MAX_LENGTH = 10000;

  @Column(length = MAX_LENGTH)
  private String requirement;

  public ProjectRequirement(String requirement) {
    this.requirement = requirement;
  }
}
