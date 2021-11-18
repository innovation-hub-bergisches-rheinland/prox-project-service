package de.innovationhub.prox.projectservice.project;


import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatorID {

  @Column(updatable = false)
  @NotNull
  private UUID creatorID;

  public CreatorID(UUID creatorID) {
    this.creatorID = creatorID;
  }
}
