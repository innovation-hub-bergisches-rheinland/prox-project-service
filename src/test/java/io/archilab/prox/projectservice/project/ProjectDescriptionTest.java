package io.archilab.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.archilab.prox.projectservice.generic.EntityStringLengthValidationTest;
import org.junit.jupiter.api.Test;

public class ProjectDescriptionTest {

  private static final int MAX_LENGTH = 10000;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<ProjectDescription> entityStringLengthValidationTest = new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(ProjectDescription::new, ProjectDescription::isValid, IllegalArgumentException.class);
  }

  @Test
  public void equality() {
    String description = "Lorem ipsum";
    ProjectDescription projectDescription1 = new ProjectDescription(description);
    ProjectDescription projectDescription2 = new ProjectDescription(description);
    assertThat(projectDescription1).isEqualTo(projectDescription2);
    assertThat(projectDescription1.hashCode()).isEqualTo(projectDescription2.hashCode());
  }
}
