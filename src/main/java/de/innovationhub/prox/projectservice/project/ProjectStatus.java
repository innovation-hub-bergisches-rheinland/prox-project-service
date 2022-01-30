package de.innovationhub.prox.projectservice.project;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: serialize/deserialize the english representation
public enum ProjectStatus {
  @JsonProperty("VERFÜGBAR")
  @JsonAlias({"VERFÜGBAR"})
  AVAILABLE,
  @JsonProperty("LAUFEND")
  @JsonAlias({"LAUFEND"})
  RUNNING,
  @JsonProperty("ABGESCHLOSSEN")
  @JsonAlias({"ABGESCHLOSSEN"})
  FINISHED
}
