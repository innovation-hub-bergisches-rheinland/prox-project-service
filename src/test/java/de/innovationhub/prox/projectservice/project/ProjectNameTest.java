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

package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.generic.EntityStringLengthValidationTest;
import org.junit.jupiter.api.Test;

public class ProjectNameTest {

  private static final int MAX_LENGTH = 255;

  @Test
  void testStringValidation() {
    EntityStringLengthValidationTest<ProjectName> entityStringLengthValidationTest =
        new EntityStringLengthValidationTest<>(MAX_LENGTH);
    entityStringLengthValidationTest.testStringValidation(
        ProjectName::new, ProjectName::isValid, IllegalArgumentException.class);
  }

  @Test
  public void equality() {
    String name = "name";
    ProjectName projectName1 = new ProjectName(name);
    ProjectName projectName2 = new ProjectName(name);
    assertThat(projectName1).isEqualTo(projectName2);
    assertThat(projectName1.hashCode()).isEqualTo(projectName2.hashCode());
  }
}
