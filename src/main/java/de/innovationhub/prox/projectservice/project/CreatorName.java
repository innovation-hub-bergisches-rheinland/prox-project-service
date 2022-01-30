package de.innovationhub.prox.projectservice.project;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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
public class CreatorName {

  private static final int MAX_LENGTH = 255;

  @JsonProperty(access = Access.READ_ONLY)
  @Column(length = MAX_LENGTH)
  private String creatorName;

  public CreatorName(String creatorName) {
    this.creatorName = creatorName;
  }
}
