package io.archilab.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ProjectNameTest {

  @Test
  public void equality() {
    String name = "name";
    ProjectName projectName1 = new ProjectName(name);
    ProjectName projectName2 = new ProjectName(name);
    assertThat(projectName1).isEqualTo(projectName2);
    assertThat(projectName1.hashCode()).isEqualTo(projectName2.hashCode());
  }
}
