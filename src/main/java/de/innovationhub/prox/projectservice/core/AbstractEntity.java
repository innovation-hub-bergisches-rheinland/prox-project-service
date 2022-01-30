package de.innovationhub.prox.projectservice.core;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@MappedSuperclass
@Data
@Setter(AccessLevel.NONE)
public class AbstractEntity {

  @Id
  @JsonProperty(access = Access.READ_ONLY)
  private UUID id;

  protected AbstractEntity() {
    this.id = UUID.randomUUID();
  }
}
