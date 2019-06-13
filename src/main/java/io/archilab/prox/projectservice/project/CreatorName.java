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
public class CreatorName {

  private static final int MAX_LENGTH = 255;

  private String creatorName;

  public CreatorName(String creatorName) {
    if (!CreatorName.isValid(creatorName)) {
      throw new IllegalArgumentException(
          String.format("Name %s exceeded maximum number of %d allowed characters", creatorName,
              CreatorName.MAX_LENGTH));
    }
    this.creatorName = creatorName;
  }

  public static boolean isValid(String name) {
    return name != null && name.length() <= CreatorName.MAX_LENGTH;
  }
}
