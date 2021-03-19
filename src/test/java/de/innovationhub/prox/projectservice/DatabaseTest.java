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

//@DataJpaTest
//public class DatabaseTest {
//
//  @Autowired ProjectRepository projectRepository;
//
//  @Autowired
//  ModuleTypeRepository moduleTypeRepository;
//
//  @Autowired
//  StudyProgramRepository studyProgramRepository;
//
//  @Test
//  public void creation() {
//    StudyProgram computerScience = new StudyProgram("CS", "Master Computer Science");
//    StudyProgram softwareEngineering = new StudyProgram("SE", "Master Software Engineering");
//    StudyProgram informationSystems = new StudyProgram("IS", "Master Information Systems");
//
//    ModuleType am = new ModuleType("AM", "Anforderungsmanagement", Collections.singletonList(softwareEngineering));
//    ModuleType fae = new ModuleType("FAE", "Fachspezifischer Architekturentwurf", Arrays.asList(softwareEngineering, informationSystems));
//    ModuleType eam = new ModuleType("EAM", "Enterprise Architecture Management", Arrays.asList(softwareEngineering));
//
//    this.moduleTypeRepository.save(am);
//    this.moduleTypeRepository.save(fae);
//    this.moduleTypeRepository.save(eam);
//
//    this.studyProgramRepository.save(computerScience);
//    this.studyProgramRepository.save(softwareEngineering);
//    this.studyProgramRepository.save(informationSystems);
//
//    assertThat(this.studyProgramRepository.findAll())
//        .contains(computerScience, softwareEngineering, informationSystems);
//    assertThat(this.moduleTypeRepository.findAll()).contains(am, fae, eam);
//
//    ArrayList<ModuleType> modules = new ArrayList<>();
//    modules.add(am);
//    Project p1 =
//        new Project(
//            new ProjectName("Projekt 1"),
//            new ProjectShortDescription("P1"),
//            new ProjectDescription("Ein neues Projekt 1"),
//            ProjectStatus.VERFÜGBAR,
//            new ProjectRequirement("R1"),
//            new CreatorID(UUID.randomUUID()),
//            new CreatorName("Creator 1"),
//            new SupervisorName("Supervisor Professor 1"),
//            modules);
//
//    modules = new ArrayList<>();
//    modules.add(fae);
//    Project p2 =
//        new Project(
//            new ProjectName("Projekt 1"),
//            new ProjectShortDescription("P2"),
//            new ProjectDescription("Ein neues Projekt 2"),
//            ProjectStatus.VERFÜGBAR,
//            new ProjectRequirement("R2"),
//            new CreatorID(UUID.randomUUID()),
//            new CreatorName("Creator 3"),
//            new SupervisorName("Supervisor Professor 3"),
//            modules);
//
//    modules = new ArrayList<>();
//    modules.add(eam);
//    Project p3 =
//        new Project(
//            new ProjectName("Projekt 1"),
//            new ProjectShortDescription("P3"),
//            new ProjectDescription("Ein neues Projekt 3"),
//            ProjectStatus.VERFÜGBAR,
//            new ProjectRequirement("R3"),
//            new CreatorID(UUID.randomUUID()),
//            new CreatorName("Creator 4"),
//            new SupervisorName("Supervisor Professor 4"),
//            modules);
//
//    this.projectRepository.save(p1);
//    this.projectRepository.save(p2);
//    this.projectRepository.save(p3);
//  }
//}
