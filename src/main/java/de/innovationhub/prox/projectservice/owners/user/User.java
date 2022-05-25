package de.innovationhub.prox.projectservice.owners.user;

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
@DiscriminatorValue("user")
@Getter
@Setter
public class User extends AbstractOwner {
  public User(UUID id) {
    super(id);
  }
}
