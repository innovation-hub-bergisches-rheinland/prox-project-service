package de.innovationhub.prox.projectservice.module;

import de.innovationhub.prox.projectservice.core.AbstractEntity;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
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
  @Max(value = 10)
  @NaturalId
  private String key;

  @NotBlank
  @Max(value = 255)
  private String name;
}
