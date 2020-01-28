package io.archilab.prox.projectservice.project;

import io.archilab.prox.projectservice.module.*;
import io.archilab.prox.projectservice.module.Module;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
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

    Assert.assertEquals(1, this.studyCourseRepository.count());

    StudyCourse emptyStudyCourse =
        new StudyCourse(new StudyCourseName("Empty"), AcademicDegree.MASTER);
    this.studyCourseRepository.save(emptyStudyCourse);

    Assert.assertEquals(2, this.studyCourseRepository.count());

    StudyCourseService service =
        new StudyCourseService(null, this.moduleRepository, this.studyCourseRepository);

    service.cleanUp();

    Assert.assertEquals(1, this.studyCourseRepository.count());

    StudyCourse result = this.studyCourseRepository.findById(moduleStudyCourse.getId()).get();

    Assert.assertNotNull(result);
  }
}
