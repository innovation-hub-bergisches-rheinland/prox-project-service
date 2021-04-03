/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.projectservice;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.ModuleTypeRepository;
import de.innovationhub.prox.projectservice.module.StudyProgram;
import de.innovationhub.prox.projectservice.module.StudyProgramRepository;
import de.innovationhub.prox.projectservice.project.CreatorID;
import de.innovationhub.prox.projectservice.project.CreatorName;
import de.innovationhub.prox.projectservice.project.Project;
import de.innovationhub.prox.projectservice.project.ProjectDescription;
import de.innovationhub.prox.projectservice.project.ProjectName;
import de.innovationhub.prox.projectservice.project.ProjectRepository;
import de.innovationhub.prox.projectservice.project.ProjectRequirement;
import de.innovationhub.prox.projectservice.project.ProjectShortDescription;
import de.innovationhub.prox.projectservice.project.ProjectStatus;
import de.innovationhub.prox.projectservice.project.SupervisorName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DatabaseTest {
  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  ModuleTypeRepository moduleTypeRepository;

  @Autowired
  StudyProgramRepository studyProgramRepository;

  @Test
  void creation() {

    ModuleType am = new ModuleType("AM", "Anforderungsmanagement");
    ModuleType fae = new ModuleType("FAE", "Fachspezifischer Architekturentwurf");
    ModuleType eam = new ModuleType("EAM", "Enterprise Architecture Management");


    StudyProgram computerScience = new StudyProgram("CS", "Master Computer Science", Set.of(am, fae));
    StudyProgram softwareEngineering = new StudyProgram("SE", "Master Software Engineering", Set.of(am, fae));
    StudyProgram informationSystems = new StudyProgram("IS", "Master Information Systems",
        Collections.singleton(eam));

    this.moduleTypeRepository.save(am);
    this.moduleTypeRepository.save(fae);
    this.moduleTypeRepository.save(eam);

    this.studyProgramRepository.save(computerScience);
    this.studyProgramRepository.save(softwareEngineering);
    this.studyProgramRepository.save(informationSystems);

    assertThat(this.studyProgramRepository.findAll())
        .contains(computerScience, softwareEngineering, informationSystems);
    assertThat(this.moduleTypeRepository.findAll()).contains(am, fae, eam);

    Set<ModuleType> modules = new HashSet<>();
    modules.add(am);
    Project p1 =
        new Project(
            new ProjectName("Projekt 1"),
            new ProjectShortDescription("P1"),
            new ProjectDescription("Ein neues Projekt 1"),
            ProjectStatus.VERFÜGBAR,
            new ProjectRequirement("R1"),
            new CreatorID(UUID.randomUUID()),
            new CreatorName("Creator 1"),
            new SupervisorName("Supervisor Professor 1"),
            modules);

    modules = new HashSet<>();
    modules.add(fae);
    Project p2 =
        new Project(
            new ProjectName("Projekt 1"),
            new ProjectShortDescription("P2"),
            new ProjectDescription("Ein neues Projekt 2"),
            ProjectStatus.VERFÜGBAR,
            new ProjectRequirement("R2"),
            new CreatorID(UUID.randomUUID()),
            new CreatorName("Creator 3"),
            new SupervisorName("Supervisor Professor 3"),
            modules);

    modules = new HashSet<>();
    modules.add(eam);
    Project p3 =
        new Project(
            new ProjectName("Projekt 1"),
            new ProjectShortDescription("P3"),
            new ProjectDescription("Ein neues Projekt 3"),
            ProjectStatus.VERFÜGBAR,
            new ProjectRequirement("R3"),
            new CreatorID(UUID.randomUUID()),
            new CreatorName("Creator 4"),
            new SupervisorName("Supervisor Professor 4"),
            modules);

    this.projectRepository.save(p1);
    this.projectRepository.save(p2);
    this.projectRepository.save(p3);
  }
}
