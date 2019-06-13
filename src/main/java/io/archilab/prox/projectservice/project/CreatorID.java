package io.archilab.prox.projectservice.project;

import java.util.UUID;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatorID {

  private UUID creatorID;

  public CreatorID(UUID creatorID) {
    this.creatorID = creatorID;
  }
}
