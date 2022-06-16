package de.innovationhub.prox.projectservice.project;


import java.util.UUID;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Supervisor {
  private UUID id;
  private String name;
}
