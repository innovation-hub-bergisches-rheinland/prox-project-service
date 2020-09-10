package io.archilab.prox.projectservice.module;

import static org.junit.jupiter.api.Assertions.*;

import io.archilab.prox.projectservice.generic.EntityStringLengthValidationTest;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction.MAX;
import org.junit.jupiter.api.Test;

class ModuleNameTest {

  private static final int MAX_LENGTH = 255;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<ModuleName> entityStringLengthValidationTest = new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(ModuleName::new, ModuleName::isValid, IllegalArgumentException.class);
  }
}