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
public class CreatorName {

  private static final int MAX_LENGTH = 255;

  @Column(length = MAX_LENGTH)
  private String creatorName;

  public CreatorName(String creatorName) {
    this.creatorName = creatorName;
  }
}
