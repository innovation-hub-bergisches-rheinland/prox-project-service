package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProjectTest {

  @Autowired ModuleTypeRepository moduleTypeRepository;

  @Autowired ProjectRepository projectRepository;

  @Test
  public void equality() {
    // Create Modules for Project
    Set<ModuleType> modules = new HashSet<>();
    modules.add(new ModuleType("M1", "Module 1"));
    modules.add(new ModuleType("M2", "Module 2"));
    modules.add(new ModuleType("M3", "Module 3"));
    this.moduleTypeRepository.saveAll(modules);
    assertThat(this.moduleTypeRepository.count()).isEqualTo(3);

    // Create Project
    Project project =
        new Project(
            new ProjectName("Testprojekt"),
            new ProjectShortDescription("Best Proj."),
            new ProjectDescription("Bestes Projekt"),
            ProjectStatus.LAUFEND,
            new ProjectRequirement("PhD"),
            UUID.randomUUID(),
            new CreatorName("Jann"),
            new SupervisorName("Jann"),
            modules,
            ProjectContext.PROFESSOR);

    this.projectRepository.save(project);
    assertTrue(this.projectRepository.findById(project.getId()).isPresent());
  }
}
