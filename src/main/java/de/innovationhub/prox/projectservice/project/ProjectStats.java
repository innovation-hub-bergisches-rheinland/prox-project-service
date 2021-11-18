package de.innovationhub.prox.projectservice.project;


import lombok.Value;

@Value
public class ProjectStats {
  int sumRunningProjects, sumFinishedProjects, sumAvailableProjects;
}
