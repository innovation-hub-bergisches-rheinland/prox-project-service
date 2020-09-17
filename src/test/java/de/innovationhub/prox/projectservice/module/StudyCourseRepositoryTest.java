package de.innovationhub.prox.projectservice.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class StudyCourseRepositoryTest {

  @Autowired StudyCourseRepository studyCourseRepository;

  static List<StudyCourse> sampleStudyCourses = new ArrayList<>();
  static StudyCourse sampleStudyCourse;

  @BeforeAll
  static void createSampleModules() {
    sampleStudyCourse =
        new StudyCourse(new StudyCourseName("Example Studycourse"), AcademicDegree.BACHELOR);
    sampleStudyCourses.add(sampleStudyCourse);
    sampleStudyCourses.add(
        new StudyCourse(new StudyCourseName("Example Studycourse 2"), AcademicDegree.BACHELOR));
    sampleStudyCourses.add(
        new StudyCourse(new StudyCourseName("Example Studycourse 3"), AcademicDegree.MASTER));
  }

  void saveSampleStudyCourse() {
    studyCourseRepository.save(sampleStudyCourse);

    Optional<StudyCourse> optionalStudyCourse =
        studyCourseRepository.findById(sampleStudyCourse.getId());
    assertTrue(optionalStudyCourse.isPresent());
    StudyCourse foundStudyCourse = optionalStudyCourse.get();
    assertEquals(sampleStudyCourse, foundStudyCourse);
  }

  @Test
  void when_project_saved_then_found_and_equal() {
    saveSampleStudyCourse();
  }

  @Test
  void when_project_updated_then_found_and_equal() {
    saveSampleStudyCourse();

    // Copy StudyCourse to maintain integrity of sampleStudyCourse
    StudyCourse copiedStudyCourse = sampleStudyCourse;

    copiedStudyCourse.setAcademicDegree(AcademicDegree.MASTER);
    copiedStudyCourse.setName(new StudyCourseName("ChangedName"));

    studyCourseRepository.save(copiedStudyCourse);
    Optional<StudyCourse> optionalStudyCourse =
        studyCourseRepository.findById(copiedStudyCourse.getId());
    assertTrue(optionalStudyCourse.isPresent());
    StudyCourse foundStudyCourse = optionalStudyCourse.get();
    assertEquals(copiedStudyCourse, foundStudyCourse);
  }

  @Test
  void when_project_deleted_then_not_found() {
    saveSampleStudyCourse();

    studyCourseRepository.delete(sampleStudyCourse);

    Optional<StudyCourse> optionalStudyCourse =
        studyCourseRepository.findById(sampleStudyCourse.getId());
    assertFalse(optionalStudyCourse.isPresent());
  }

  @Test
  void when_find_by_external_module_id_is_valid_then_found()
      throws URISyntaxException, MalformedURLException {

    StudyCourse copiedStudyCourse = sampleStudyCourse;
    copiedStudyCourse.setExternalStudyCourseID(
        new ExternalStudyCourseID(new URI("http://example.org").toURL()));
    studyCourseRepository.save(copiedStudyCourse);
    Optional<StudyCourse> optionalStudyCourse =
        studyCourseRepository.findById(copiedStudyCourse.getId());

    assertTrue(optionalStudyCourse.isPresent());
    assertEquals(copiedStudyCourse, optionalStudyCourse.get());

    optionalStudyCourse =
        studyCourseRepository.findByExternalStudyCourseID(
            copiedStudyCourse.getExternalStudyCourseID());

    assertTrue(optionalStudyCourse.isPresent());
    assertEquals(copiedStudyCourse, optionalStudyCourse.get());
  }

  @Test
  void when_find_by_academic_degree_then_found() {
    studyCourseRepository.saveAll(sampleStudyCourses);

    List<StudyCourse> foundStudyCourses =
        studyCourseRepository.findByAcademicDegree(AcademicDegree.BACHELOR);

    assertEquals(
        sampleStudyCourses.stream()
            .filter(it -> it.getAcademicDegree() == AcademicDegree.BACHELOR)
            .count(),
        foundStudyCourses.size());
  }
}
