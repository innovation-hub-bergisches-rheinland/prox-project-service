package de.innovationhub.prox.projectservice.owners.organization;

import static de.innovationhub.prox.projectservice.owners.organization.Organization.DISCRIMINATOR;

import de.innovationhub.prox.projectservice.owners.AbstractOwner;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(DISCRIMINATOR)
@Getter
@Setter
public class Organization extends AbstractOwner {

  public static final String DISCRIMINATOR = "organization";
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<UUID> members = new HashSet<>();

  public Organization(UUID id, String name) {
    super(id, DISCRIMINATOR, name);
  }

  @Override
  public String getDiscriminator() {
    return DISCRIMINATOR;
  }
}
