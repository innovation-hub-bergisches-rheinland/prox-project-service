package de.innovationhub.prox.projectservice.owners.organization;

import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("organization")
@Getter
@Setter
public class Organization extends AbstractOwner {

}
