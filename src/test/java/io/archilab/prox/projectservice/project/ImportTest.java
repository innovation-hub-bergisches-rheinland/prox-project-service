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

import io.archilab.prox.projectservice.module.AcademicDegree;
import io.archilab.prox.projectservice.module.Module;
import io.archilab.prox.projectservice.module.ModuleName;
import io.archilab.prox.projectservice.module.ModuleRepository;
import io.archilab.prox.projectservice.module.ProjectType;
import io.archilab.prox.projectservice.module.StudyCourse;
import io.archilab.prox.projectservice.module.StudyCourseName;
import io.archilab.prox.projectservice.module.StudyCourseRepository;
import io.archilab.prox.projectservice.module.StudyCourseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ImportTest {

  @Autowired ModuleRepository moduleRepository;

  @Autowired StudyCourseRepository studyCourseRepository;

  @Test
  public void cleanup() {

    // Create Modules for Project
    Module m1 = new Module(new ModuleName("Module 1"), ProjectType.UNDEFINED);
    Module m2 = new Module(new ModuleName("Module 2"), ProjectType.UNDEFINED);
    this.moduleRepository.save(m1);
    this.moduleRepository.save(m2);

    StudyCourse moduleStudyCourse =
        new StudyCourse(new StudyCourseName("SC"), AcademicDegree.MASTER);
    moduleStudyCourse.addModule(m1);
    moduleStudyCourse.addModule(m2);
    this.studyCourseRepository.save(moduleStudyCourse);

    Assertions.assertEquals(1, this.studyCourseRepository.count());

    StudyCourse emptyStudyCourse =
        new StudyCourse(new StudyCourseName("Empty"), AcademicDegree.MASTER);
    this.studyCourseRepository.save(emptyStudyCourse);

    Assertions.assertEquals(2, this.studyCourseRepository.count());

    StudyCourseService service =
        new StudyCourseService(null, this.moduleRepository, this.studyCourseRepository);

    service.cleanUp();

    Assertions.assertEquals(1, this.studyCourseRepository.count());

    StudyCourse result =
        this.studyCourseRepository.findById(moduleStudyCourse.getId()).orElseThrow();

    Assertions.assertNotNull(result);
  }
}
