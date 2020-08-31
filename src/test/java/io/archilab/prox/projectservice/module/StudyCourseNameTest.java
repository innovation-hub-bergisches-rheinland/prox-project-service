package io.archilab.prox.projectservice.module;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class StudyCourseNameTest {

  private static final int MAX_LENGTH = 255;
  private static final String valid_name = "a".repeat(MAX_LENGTH);
  private static final String invalid_name = "a".repeat(MAX_LENGTH + 1);

  @SuppressWarnings("ConstantConditions")
  @Test
  void when_name_is_null_then_not_valid() {
    assertFalse(StudyCourseName.isValid(null));
  }

  @Test
  void when_name_length_is_equal_to_max_length_then_is_valid() {
    assertTrue(StudyCourseName.isValid(valid_name));
  }

  @Test
  void when_name_length_is_longer_than_max_length_then_is_not_valid() {
    assertFalse(StudyCourseName.isValid(invalid_name));
  }

  @Test
  void when_new_study_course_name_is_instantiated_with_valid_name_then_does_not_throw_exception() {
    assertDoesNotThrow(() -> new StudyCourseName(valid_name));
  }

  @Test
  void when_new_study_course_name_is_instantiated_with_invalid_name_then_throw_exception() {
    assertThrows(IllegalArgumentException.class, () -> new StudyCourseName(invalid_name));
  }
}