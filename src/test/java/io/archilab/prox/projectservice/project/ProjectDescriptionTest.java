/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
