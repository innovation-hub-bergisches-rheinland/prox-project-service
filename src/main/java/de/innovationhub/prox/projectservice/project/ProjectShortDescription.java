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
public class ProjectShortDescription {

  private static final int MAX_LENGTH = 10000;

  @Column(length = MAX_LENGTH)
  @Size(min = 1, max = MAX_LENGTH)
  @NotBlank
  private String shortDescription;

  public ProjectShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }
}
