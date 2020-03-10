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
