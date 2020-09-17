package de.innovationhub.prox.projectservice.module;


import de.innovationhub.prox.projectservice.generic.EntityStringLengthValidationTest;
import org.junit.jupiter.api.Test;

class StudyCourseNameTest {

  private static final int MAX_LENGTH = 255;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<StudyCourseName> entityStringLengthValidationTest =
        new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(
        StudyCourseName::new, StudyCourseName::isValid, IllegalArgumentException.class);
  }
}
