package de.innovationhub.prox.projectservice.owners;

import de.innovationhub.prox.projectservice.core.AbstractEntity;
import de.innovationhub.prox.projectservice.project.Project;
import java.util.List;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
// Use single table as inheritance strategy. This might result in a better query performance and
// should be the simplest to use
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="owner_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "abstract_owner")
public abstract class AbstractOwner extends AbstractEntity {

}
