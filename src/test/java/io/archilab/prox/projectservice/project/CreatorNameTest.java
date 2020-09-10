package io.archilab.prox.projectservice.project;

import static org.junit.jupiter.api.Assertions.*;

import io.archilab.prox.projectservice.generic.EntityStringLengthValidationTest;
import io.archilab.prox.projectservice.module.StudyCourseName;
import org.junit.jupiter.api.Test;

class CreatorNameTest {

  private static final int MAX_LENGTH = 255;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<CreatorName> entityStringLengthValidationTest = new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(CreatorName::new, CreatorName::isValid, IllegalArgumentException.class);
  }
}