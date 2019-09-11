package io.archilab.prox.projectservice.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class StudyCourseService {

  private final Logger logger = LoggerFactory.getLogger(StudyCourseService.class);

  private final StudyCourseClient studyCourseClient;
  private final ModuleRepository moduleRepository;
  private final StudyCourseRepository studyCourseRepository;


  public StudyCourseService(StudyCourseClient studyCourseClient, ModuleRepository moduleRepository,
      StudyCourseRepository studyCourseRepository) {
    this.studyCourseClient = studyCourseClient;
    this.moduleRepository = moduleRepository;

    this.studyCourseRepository = studyCourseRepository;
  }

  public boolean hasData() {
    return studyCourseRepository.count() > 0;
  }

  public void importStudyCourses() {
    this.logger.info("Start importing Study Courses");

    List<StudyCourse> studyCourses = this.studyCourseClient.getStudyCourses();
    for (StudyCourse studyCourse : studyCourses) {
      Optional<StudyCourse> existingStudyCourseOptional = this.studyCourseRepository
          .findByExternalStudyCourseID(studyCourse.getExternalStudyCourseID());

      if (existingStudyCourseOptional.isPresent()) {
        this.logger.info(
            "StudyCourse with ID " + studyCourse.getExternalStudyCourseID() + " already exists.");
        StudyCourse existingStudyCourse = existingStudyCourseOptional.get();
        existingStudyCourse.setName(studyCourse.getName());
        existingStudyCourse.setAcademicDegree(studyCourse.getAcademicDegree());
        this.studyCourseRepository.save(existingStudyCourse);
      } else {
        this.logger.info("StudyCourse with ID " + studyCourse.getExternalStudyCourseID()
            + " does not exist yet.");
        this.studyCourseRepository.save(studyCourse);
      }

      List<Module> retrievedModules = studyCourse.getModules();
      for (Module module : retrievedModules) {
        Optional<Module> existingModuleOptional =
            this.moduleRepository.findByExternalModuleID(module.getExternalModuleID());

        if (existingModuleOptional.isPresent()) {
          this.logger.info("Module with ID " + module.getExternalModuleID() + " already exists.");
          Module existingModule = existingModuleOptional.get();
          existingModule.setName(module.getName());
          existingModule.setProjectType(module.getProjectType());
          existingModule.setStudyCourse(studyCourse);
          this.moduleRepository.save(existingModule);
        } else {
          this.logger
              .info("Module with ID " + module.getExternalModuleID() + " does not exist yet.");
          module.setStudyCourse(studyCourse);
          this.moduleRepository.save(module);
        }
      }
    }
    this.logger.info("Finished import");
  }
}
