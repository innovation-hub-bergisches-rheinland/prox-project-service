package de.innovationhub.prox.projectservice.project;

import java.util.HashMap;
import lombok.Value;

@Value
public class ProjectStats {
  int sumRunningProjects, sumFinishedProjects, sumAvailableProjects;
}
