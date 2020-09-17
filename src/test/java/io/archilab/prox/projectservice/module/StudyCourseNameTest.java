package io.archilab.prox.projectservice.module;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.when;

import io.archilab.prox.projectservice.generic.EntityStringLengthValidationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class StudyCourseNameTest {

  private static final int MAX_LENGTH = 255;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<StudyCourseName> entityStringLengthValidationTest = new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(StudyCourseName::new, StudyCourseName::isValid, IllegalArgumentException.class);
  }
}