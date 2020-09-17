package de.innovationhub.prox.projectservice.module;

import de.innovationhub.prox.projectservice.generic.EntityStringLengthValidationTest;
import org.junit.jupiter.api.Test;

class ModuleNameTest {

  private static final int MAX_LENGTH = 255;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<ModuleName> entityStringLengthValidationTest =
        new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(
        ModuleName::new, ModuleName::isValid, IllegalArgumentException.class);
  }
}
