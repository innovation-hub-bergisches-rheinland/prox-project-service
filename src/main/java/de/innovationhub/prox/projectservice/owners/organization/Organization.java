package de.innovationhub.prox.projectservice.owners.organization;

import static de.innovationhub.prox.projectservice.owners.organization.Organization.DISCRIMINATOR;

import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import java.util.UUID;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(DISCRIMINATOR)
@Getter
@Setter
public class Organization extends AbstractOwner {
  public final static String DISCRIMINATOR = "organization";
  public Organization(UUID id) {
    super(id);
  }

  @Override
  public String getDiscriminator() {
    return DISCRIMINATOR;
  }
}
