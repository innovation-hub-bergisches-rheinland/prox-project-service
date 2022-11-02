package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.AbstractDatabaseIT;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.module.SpecializationRepository;
import de.innovationhub.prox.projectservice.owners.user.User;
import de.innovationhub.prox.projectservice.project.mapper.ProjectMapper;
import de.innovationhub.prox.projectservice.security.OwnerPermissionEvaluatorHelper;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ProjectServiceTest extends AbstractDatabaseIT {
  @Autowired
  ProjectService projectService;

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  SpecializationRepository specializationRepository;

  @Autowired
  EntityManager em;

  @MockBean
  OwnerPermissionEvaluatorHelper ownerPermissionEvaluatorHelper;

  @Autowired
  PlatformTransactionManager transactionManager;
  TransactionTemplate transactionTemplate;

  @BeforeEach
  void setup() {
    when(ownerPermissionEvaluatorHelper.hasPermissionWithCurrentContext(any())).thenReturn(true);
    transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Test
  void shouldReturnProjectWithCorrectAmountOfTags() throws Exception {
    var project = createSampleProject();
    project.setTags(List.of("tag1", "tag2"));
    project.setTags(List.of("tag1", "tag2", "tag3"));
    var savedProject = transactionTemplate.execute(status -> projectRepository.save(project));

    var projectWithTags = transactionTemplate.execute(status -> projectService.get(savedProject.getId()));
    var allProjects = transactionTemplate.execute(status -> projectService.getAll());

    assertThat(projectWithTags.tags())
      .hasSize(3);
  }

  private Project createSampleProject() {
    var owner = new User(UUID.randomUUID(), "Xavier Tester");
    var savedOwner = transactionTemplate.execute(status -> {
      em.persist(owner);
      return owner;
    });
    var specializations = specializationRepository.findAllByKeyIn(List.of("INF", "ING"));

    return Project.builder()
      .name("Test")
      .description("Test")
      .status(ProjectStatus.AVAILABLE)
      .specializations(specializations)
      .ownerId(savedOwner.getId())
      .shortDescription("Test")
      .build();
  }
}
