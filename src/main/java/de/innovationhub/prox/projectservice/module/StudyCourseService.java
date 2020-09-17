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

package de.innovationhub.prox.projectservice.module;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class StudyCourseService {

  private final Logger logger = LoggerFactory.getLogger(StudyCourseService.class);

  private final StudyCourseClient studyCourseClient;
  private final ModuleRepository moduleRepository;
  private final StudyCourseRepository studyCourseRepository;

  public StudyCourseService(
      StudyCourseClient studyCourseClient,
      ModuleRepository moduleRepository,
      StudyCourseRepository studyCourseRepository) {
    this.studyCourseClient = studyCourseClient;
    this.moduleRepository = moduleRepository;
    this.studyCourseRepository = studyCourseRepository;
  }

  public boolean hasData() {
    return this.studyCourseRepository.count() > 0;
  }

  public void importStudyCourses() {
    this.logger.info("Start importing Study Courses");

    List<StudyCourse> studyCourses = this.studyCourseClient.getStudyCourses();
    for (StudyCourse studyCourse : studyCourses) {
      Optional<StudyCourse> existingStudyCourseOptional =
          this.studyCourseRepository.findByExternalStudyCourseID(
              studyCourse.getExternalStudyCourseID());
      StudyCourse savedStudyCourse = null;

      if (existingStudyCourseOptional.isPresent()) {
        this.logger.info(
            "StudyCourse with ID {} already exists.", studyCourse.getExternalStudyCourseID());
        savedStudyCourse = existingStudyCourseOptional.get();
      } else {
        this.logger.info(
            "StudyCourse with ID {} does not exist yet.", studyCourse.getExternalStudyCourseID());
        savedStudyCourse = new StudyCourse();
      }

      savedStudyCourse.setName(studyCourse.getName());
      savedStudyCourse.setAcademicDegree(studyCourse.getAcademicDegree());
      savedStudyCourse.setExternalStudyCourseID(studyCourse.getExternalStudyCourseID());
      savedStudyCourse = this.studyCourseRepository.save(savedStudyCourse);

      List<Module> retrievedModules = studyCourse.getModules();
      for (Module module : retrievedModules) {
        Optional<Module> existingModuleOptional =
            this.moduleRepository.findByExternalModuleID(module.getExternalModuleID());

        if (existingModuleOptional.isPresent()) {
          this.logger.info("Module with ID {} already exists.", module.getExternalModuleID());
          Module existingModule = existingModuleOptional.get();
          existingModule.setName(module.getName());
          existingModule.setProjectType(module.getProjectType());
          existingModule.setStudyCourse(savedStudyCourse);
          this.moduleRepository.save(existingModule);
        } else {
          this.logger.info("Module with ID {} does not exist yet.", module.getExternalModuleID());
          module.setStudyCourse(savedStudyCourse);
          this.moduleRepository.save(module);
        }
      }
    }

    this.logger.info("Finished import");
  }

  public void cleanUp() {

    // delete all study courses without modules
    Iterable<StudyCourse> studyCourses = this.studyCourseRepository.findAll();

    for (StudyCourse studyCourse : studyCourses) {
      if (studyCourse.getModules().isEmpty()) {
        this.studyCourseRepository.delete(studyCourse);
      }
    }
  }
}
