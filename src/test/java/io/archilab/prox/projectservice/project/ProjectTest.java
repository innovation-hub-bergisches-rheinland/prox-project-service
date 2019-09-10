package io.archilab.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;
import io.archilab.prox.projectservice.module.Module;
import io.archilab.prox.projectservice.module.ModuleName;
import io.archilab.prox.projectservice.module.ModuleRepository;
import io.archilab.prox.projectservice.module.ProjectType;
import io.archilab.prox.projectservice.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProjectTest {

  @Autowired
  ModuleRepository moduleRepository;

  @Autowired
  ProjectRepository projectRepository;

  @Test
  public void equality() {
    // Create Modules for Project
    List<Module> modules = new ArrayList<>();
    modules.add(new Module(new ModuleName("Module 1"), ProjectType.UNDEFINED));
    modules.add(new Module(new ModuleName("Module 2"), ProjectType.UNDEFINED));
    modules.add(new Module(new ModuleName("Module 3"), ProjectType.UNDEFINED));
    this.moduleRepository.saveAll(modules);
    assertThat(this.moduleRepository.count()).isEqualTo(3);

    // Create Project
    Project project = new Project(new ProjectName("Testprojekt"),
        new ProjectShortDescription("Best Proj."), new ProjectDescription("Bestes Projekt"),
        ProjectStatus.LAUFEND, new ProjectRequirement("PhD"), new CreatorID(UUID.randomUUID()),
        new CreatorName("Jann"), new SupervisorName("Jann"), modules, new ArrayList<Tag>());

    this.projectRepository.save(project);
    assertThat(this.projectRepository.findById(project.getId()).isPresent()).isEqualTo(true);
  }
}
