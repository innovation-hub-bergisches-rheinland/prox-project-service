package io.archilab.prox.projectservice.project;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectShortDescription {

  private String shortDescription;

  public ProjectShortDescription(String shortDescription) {
    if (!ProjectShortDescription.isValid(shortDescription)) {
      throw new IllegalArgumentException(
          String.format("Short description is empty", shortDescription));
    }
    this.shortDescription = shortDescription;
  }

  public static boolean isValid(String name) {
    return name != null;
  }
}
