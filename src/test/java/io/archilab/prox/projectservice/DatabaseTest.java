package io.archilab.prox.projectservice;

import static org.assertj.core.api.Assertions.assertThat;
import io.archilab.prox.projectservice.module.AcademicDegree;
import io.archilab.prox.projectservice.module.Module;
import io.archilab.prox.projectservice.module.ModuleName;
import io.archilab.prox.projectservice.module.ModuleRepository;
import io.archilab.prox.projectservice.module.ProjectType;
import io.archilab.prox.projectservice.module.StudyCourse;
import io.archilab.prox.projectservice.module.StudyCourseName;
import io.archilab.prox.projectservice.module.StudyCourseRepository;
import io.archilab.prox.projectservice.project.*;
import io.archilab.prox.projectservice.tags.Tag;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DatabaseTest {

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  ModuleRepository moduleRepository;

  @Autowired
  StudyCourseRepository studyCourseRepository;

  @Test
  public void creation() {
    StudyCourse computerScience =
        new StudyCourse(new StudyCourseName("Computer Science"), AcademicDegree.MASTER);
    StudyCourse softwareEngineering =
        new StudyCourse(new StudyCourseName("Software Engineering"), AcademicDegree.MASTER);
    StudyCourse informationSystems =
        new StudyCourse(new StudyCourseName("Information Systems"), AcademicDegree.MASTER);

<<<<<<< HEAD
    Module am = new Module(new ModuleName("Anforderungsmanagement"),ProjectType.UNDEFINED);
    Module fae = new Module(new ModuleName("Fachspezifischer Architekturentwurf"),ProjectType.UNDEFINED);
    Module bi = new Module(new ModuleName("Business Intelligence"),ProjectType.UNDEFINED);
    Module eam = new Module(new ModuleName("Enterprise Architecture Management"),ProjectType.UNDEFINED);
    
=======
    Module am = new Module(new ModuleName("Anforderungsmanagement"), ProjectType.UNDEFINED);
    Module fae =
        new Module(new ModuleName("Fachspezifischer Architekturentwurf"), ProjectType.UNDEFINED);
    Module bi = new Module(new ModuleName("Business Intelligence"), ProjectType.UNDEFINED);
    Module eam =
        new Module(new ModuleName("Enterprise Architecture Management"), ProjectType.UNDEFINED);

>>>>>>> branch 'gp-ws2019' of https://github.com/Archi-Lab/prox-project-service
    this.moduleRepository.save(am);
    this.moduleRepository.save(fae);
    this.moduleRepository.save(bi);
    this.moduleRepository.save(eam);

    softwareEngineering.addModule(am);
    softwareEngineering.addModule(fae);

    informationSystems.addModule(bi);
    informationSystems.addModule(eam);

    this.studyCourseRepository.save(computerScience);
    this.studyCourseRepository.save(softwareEngineering);
    this.studyCourseRepository.save(informationSystems);

    assertThat(this.studyCourseRepository.findAll()).contains(computerScience, softwareEngineering,
        informationSystems);
    assertThat(this.moduleRepository.findAll()).contains(am, fae, bi, eam);

    ArrayList<Module> modules = new ArrayList<>();
    modules.add(am);
    Project p1 = new Project(new ProjectName("Projekt 1"), new ProjectShortDescription("P1"),
        new ProjectDescription("Ein neues Projekt 1"), ProjectStatus.VERFÜGBAR,
        new ProjectRequirement("R1"), new CreatorID(UUID.randomUUID()),
        new CreatorName("Creator 1"), new SupervisorName("Supervisor Professor 1"), modules,
        new ArrayList<Tag>());

    modules = new ArrayList<>();
    modules.add(fae);
    Project p2 = new Project(new ProjectName("Projekt 1"), new ProjectShortDescription("P2"),
        new ProjectDescription("Ein neues Projekt 2"), ProjectStatus.VERFÜGBAR,
        new ProjectRequirement("R2"), new CreatorID(UUID.randomUUID()),
        new CreatorName("Creator 3"), new SupervisorName("Supervisor Professor 3"), modules,
        new ArrayList<Tag>());

    modules = new ArrayList<>();
    modules.add(eam);
    Project p3 = new Project(new ProjectName("Projekt 1"), new ProjectShortDescription("P3"),
        new ProjectDescription("Ein neues Projekt 3"), ProjectStatus.VERFÜGBAR,
        new ProjectRequirement("R3"), new CreatorID(UUID.randomUUID()),
        new CreatorName("Creator 4"), new SupervisorName("Supervisor Professor 4"), modules,
        new ArrayList<Tag>());

    this.projectRepository.save(p1);
    this.projectRepository.save(p2);
    this.projectRepository.save(p3);
  }

}
