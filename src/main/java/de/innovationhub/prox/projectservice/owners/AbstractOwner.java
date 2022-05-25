package de.innovationhub.prox.projectservice.owners;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import de.innovationhub.prox.projectservice.core.AbstractEntity;
import de.innovationhub.prox.projectservice.project.Project;
import java.util.List;
import java.util.UUID;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
// Use single table as inheritance strategy. This might result in a better query performance and
// should be the simplest to use
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="owner_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "abstract_owner")
public abstract class AbstractOwner {
  @Id
  @JsonProperty(access = Access.READ_ONLY)
  @Setter(value = AccessLevel.PROTECTED)
  private UUID id;
}
