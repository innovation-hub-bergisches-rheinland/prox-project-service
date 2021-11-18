package de.innovationhub.prox.projectservice.module;


import de.innovationhub.prox.projectservice.core.AbstractEntity;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModuleType extends AbstractEntity {
  @NotBlank
  @Size(max = 10)
  @NaturalId
  private String key;

  @NotBlank
  @Size(max = 255)
  private String name;
}
