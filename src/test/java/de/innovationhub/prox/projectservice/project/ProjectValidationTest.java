package de.innovationhub.prox.projectservice.project;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootTest
class ProjectValidationTest {
  @Autowired LocalValidatorFactoryBean localValidatorFactoryBean;

  @Test
  void when_project_name_is_null_should_return_violation() {
    Project project = new Project();
    project.setName(null);

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_name_is_blank_should_return_violation() {
    Project project = new Project();
    project.setName(new ProjectName("    "));

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_short_description_is_null_should_return_violation() {
    Project project = new Project();
    project.setShortDescription(null);

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_short_description_is_blank_should_return_violation() {
    Project project = new Project();
    project.setShortDescription(new ProjectShortDescription("    "));

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_description_is_null_should_return_violation() {
    Project project = new Project();
    project.setDescription(null);

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_description_is_blank_should_return_violation() {
    Project project = new Project();
    project.setDescription(new ProjectDescription("    "));

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_status_is_null_should_return_violation() {
    Project project = new Project();
    project.setStatus(null);

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_creatorId_is_null_should_return_violation() {
    Project project = new Project();
    project.setCreatorID(null);

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_creatorId_id_is_null_should_return_violation() {
    Project project = new Project();
    project.setCreatorID(new CreatorID(null));

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_creatorname_is_null_should_return_violation() {
    Project project = new Project();
    project.setCreatorName(null);

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_creatorname_is_blank_should_return_violation() {
    Project project = new Project();
    project.setCreatorName(new CreatorName("   "));

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_supervisorname_is_null_should_return_violation() {
    Project project = new Project();
    project.setSupervisorName(null);

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }

  @Test
  void when_project_supervisorname_is_blank_should_return_violation() {
    Project project = new Project();
    project.setSupervisorName(new SupervisorName(" "));

    Set<ConstraintViolation<Project>> violationSet = localValidatorFactoryBean.validate(project);
    assertFalse(violationSet.isEmpty());
  }
}
