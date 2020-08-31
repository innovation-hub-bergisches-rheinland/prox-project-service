package io.archilab.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;

import io.archilab.prox.projectservice.generic.EntityStringLengthValidationTest;
import org.junit.jupiter.api.Test;

public class ProjectNameTest {

  private static final int MAX_LENGTH = 255;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<ProjectName> entityStringLengthValidationTest = new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(ProjectName::new, ProjectName::isValid, IllegalArgumentException.class);
  }

  @Test
  public void equality() {
    String name = "name";
    ProjectName projectName1 = new ProjectName(name);
    ProjectName projectName2 = new ProjectName(name);
    assertThat(projectName1).isEqualTo(projectName2);
    assertThat(projectName1.hashCode()).isEqualTo(projectName2.hashCode());
  }
}
