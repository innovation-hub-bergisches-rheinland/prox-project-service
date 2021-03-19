package de.innovationhub.prox.projectservice.module;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

//@DataJpaTest
//class StudyCourseServiceTest {
//
//  @Autowired StudyCourseRepository studyCourseRepository;
//
//  @Autowired ModuleRepository moduleRepository;
//
//  @MockBean StudyCourseClient studyCourseClient;
//
//  StudyCourseService studyCourseService;
//
//  List<StudyCourse> sampleStudyCourseList() throws MalformedURLException {
//
//    final List<StudyCourse> expectedStudyCourseList = new ArrayList<>();
//
//    StudyCourse wirtschaftsinformatik =
//        new StudyCourse(new StudyCourseName("Wirtschaftsinformatik"), AcademicDegree.BACHELOR);
//    Module bachelorArbeit =
//        new Module(
//            new ModuleName("Bachelor Arbeit "),
//            ProjectType.BA); // Preserve space as it is equal to the json file
//    bachelorArbeit.setExternalModuleID(new ExternalModuleID(new URL("http://example.org")));
//    Module praxisProjekt = new Module(new ModuleName("Praxisprojekt"), ProjectType.PP);
//    praxisProjekt.setExternalModuleID(new ExternalModuleID(new URL("http://example.org")));
//    wirtschaftsinformatik.setModules(
//        new ArrayList<>() {
//          {
//            add(bachelorArbeit);
//            add(praxisProjekt);
//          }
//        });
//    wirtschaftsinformatik.setExternalStudyCourseID(
//        new ExternalStudyCourseID(new URL("http://example.org")));
//
//    StudyCourse medieninformatik =
//        new StudyCourse(new StudyCourseName("Medieninformatik"), AcademicDegree.MASTER);
//    medieninformatik.setModules(
//        new ArrayList<>() {
//          {
//            add(bachelorArbeit);
//            add(praxisProjekt);
//          }
//        });
//    medieninformatik.setExternalStudyCourseID(
//        new ExternalStudyCourseID(new URL("http://example.org")));
//
//    StudyCourse computerScience =
//        new StudyCourse(new StudyCourseName("Informatik/Computer Science"), AcademicDegree.MASTER);
//    computerScience.setModules(
//        new ArrayList<>() {
//          {
//            add(bachelorArbeit);
//            add(praxisProjekt);
//          }
//        });
//    computerScience.setExternalStudyCourseID(
//        new ExternalStudyCourseID(new URL("http://example.org")));
//
//    expectedStudyCourseList.add(wirtschaftsinformatik);
//    expectedStudyCourseList.add(medieninformatik);
//    expectedStudyCourseList.add(computerScience);
//
//    return expectedStudyCourseList;
//  }
//
//  @BeforeEach
//  void setUp() throws Exception {
//    MockitoAnnotations.initMocks(StudyCourseServiceTest.class);
//    when(studyCourseClient.getStudyCourses()).thenReturn(sampleStudyCourseList());
//
//    studyCourseService =
//        new StudyCourseService(studyCourseClient, moduleRepository, studyCourseRepository);
//  }
//
//  @Test
//  void when_studycourses_saved_then_has_data() throws Exception {
//    studyCourseRepository.saveAll(sampleStudyCourseList());
//
//    assertTrue(studyCourseService.hasData());
//  }
//
//  @Test
//  void when_no_studycourses_saved_then_has_no_data() {
//    assertFalse(studyCourseService.hasData());
//  }
//
//  @Test
//  void when_study_course_import_then_all_saved() throws Exception {
//    studyCourseService.importStudyCourses();
//
//    for (StudyCourse sc : sampleStudyCourseList()) {
//      assertTrue(
//          studyCourseRepository
//              .findByExternalStudyCourseID(sc.getExternalStudyCourseID())
//              .isPresent());
//
//      for (Module module : sc.getModules()) {
//        assertTrue(
//            moduleRepository.findByExternalModuleID(module.getExternalModuleID()).isPresent());
//      }
//    }
//  }
//
//  @Test
//  void when_studycourses_with_no_modules_saved_cleanup_deletes() {
//    StudyCourse studyCourseWithNoModules1 =
//        new StudyCourse(
//            new StudyCourseName("Study Course with no modules"), AcademicDegree.BACHELOR);
//    StudyCourse studyCourseWithNoModules2 =
//        new StudyCourse(new StudyCourseName("Study Course with no modules"), AcademicDegree.MASTER);
//
//    studyCourseRepository.save(studyCourseWithNoModules1);
//    studyCourseRepository.save(studyCourseWithNoModules2);
//
//    assertTrue(studyCourseRepository.existsById(studyCourseWithNoModules1.getId()));
//    assertTrue(studyCourseRepository.existsById(studyCourseWithNoModules2.getId()));
//
//    studyCourseService.cleanUp();
//
//    assertFalse(studyCourseRepository.existsById(studyCourseWithNoModules1.getId()));
//    assertFalse(studyCourseRepository.existsById(studyCourseWithNoModules2.getId()));
//  }
//
//  @Test
//  void
//      when_studycourses_with_mixed_modules_saved_cleanup_deletes_only_study_courses_without_modules() {
//    StudyCourse studyCourseWithNoModules1 =
//        new StudyCourse(
//            new StudyCourseName("Study Course with no modules"), AcademicDegree.BACHELOR);
//    StudyCourse studyCourseWithNoModules2 =
//        new StudyCourse(new StudyCourseName("Study Course with modules"), AcademicDegree.MASTER);
//    studyCourseWithNoModules2.addModule(new Module(new ModuleName("Module"), ProjectType.BA));
//
//    studyCourseRepository.save(studyCourseWithNoModules1);
//    studyCourseRepository.save(studyCourseWithNoModules2);
//
//    assertTrue(studyCourseRepository.existsById(studyCourseWithNoModules1.getId()));
//    assertTrue(studyCourseRepository.existsById(studyCourseWithNoModules2.getId()));
//
//    studyCourseService.cleanUp();
//
//    assertFalse(studyCourseRepository.existsById(studyCourseWithNoModules1.getId()));
//    assertTrue(studyCourseRepository.existsById(studyCourseWithNoModules2.getId()));
//  }
//}
