package de.innovationhub.prox.projectservice.generic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;

public class EntityStringLengthValidationTest<T> {

  public final int MAX_LENGTH;
  private final String valid_string;
  private final String invalid_string;

  public EntityStringLengthValidationTest(int maxStringLength) {
    MAX_LENGTH = maxStringLength;
    valid_string = "a".repeat(MAX_LENGTH);
    invalid_string = "a".repeat(MAX_LENGTH + 1);
  }

  public void testStringValidation(
      Function<String, T> objectSupplier,
      Function<String, Boolean> validationFunction,
      Class<? extends Exception> exceptionClass) {
    when_string_is_null_then_not_valid(validationFunction);
    when_string_length_is_equal_to_max_length_then_is_valid(validationFunction);
    when_string_length_is_longer_than_max_length_then_is_not_valid(validationFunction);
    when_new_object_is_instantiated_with_valid_name_then_does_not_throw_exception(objectSupplier);
    when_new_module_name_is_instantiated_with_invalid_name_then_throw_exception(
        objectSupplier, exceptionClass);
  }

  void when_string_is_null_then_not_valid(Function<String, Boolean> validationFunction) {
    assertFalse(validationFunction.apply(null));
  }

  void when_string_length_is_equal_to_max_length_then_is_valid(
      Function<String, Boolean> validationFunction) {
    assertTrue(validationFunction.apply(valid_string));
  }

  void when_string_length_is_longer_than_max_length_then_is_not_valid(
      Function<String, Boolean> validationFunction) {
    assertFalse(validationFunction.apply(invalid_string));
  }

  void when_new_object_is_instantiated_with_valid_name_then_does_not_throw_exception(
      Function<String, T> objectSupplier) {
    assertDoesNotThrow(() -> objectSupplier.apply(valid_string));
  }

  void when_new_module_name_is_instantiated_with_invalid_name_then_throw_exception(
      Function<String, T> objectSupplier, Class<? extends Exception> exceptionClass) {
    assertThrows(exceptionClass, () -> objectSupplier.apply(invalid_string));
  }
}
