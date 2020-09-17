package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.generic.EntityStringLengthValidationTest;
import org.junit.jupiter.api.Test;

class CreatorNameTest {

  private static final int MAX_LENGTH = 255;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<CreatorName> entityStringLengthValidationTest =
        new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(
        CreatorName::new, CreatorName::isValid, IllegalArgumentException.class);
  }
}
