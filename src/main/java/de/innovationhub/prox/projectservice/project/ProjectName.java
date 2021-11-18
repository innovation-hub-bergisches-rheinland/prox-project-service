package de.innovationhub.prox.projectservice.project;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectName {

  private static final int MAX_LENGTH = 255;

  @Column(length = MAX_LENGTH)
  @Size(min = 1, max = MAX_LENGTH)
  @NotBlank
  @NotNull
  private String name;

  public ProjectName(String name) {
    this.name = name;
  }
}
