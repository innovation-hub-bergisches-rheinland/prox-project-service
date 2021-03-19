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

package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;

//@DataJpaTest
//public class ProjectTest {
//
//  @Autowired
//  ModuleTypeRepository moduleTypeRepository;
//
//  @Autowired ProjectRepository projectRepository;
//
//  @Test
//  public void equality() {
//    // Create Modules for Project
//    List<ModuleType> modules = new ArrayList<>();
//    modules.add(new ModuleType("M1", "Module 1", Collections.emptyList()));
//    modules.add(new ModuleType("M2", "Module 2", Collections.emptyList()));
//    modules.add(new ModuleType("M3", "Module 3", Collections.emptyList()));
//    this.moduleTypeRepository.saveAll(modules);
//    assertThat(this.moduleTypeRepository.count()).isEqualTo(3);
//
//    // Create Project
//    Project project =
//        new Project(
//            new ProjectName("Testprojekt"),
//            new ProjectShortDescription("Best Proj."),
//            new ProjectDescription("Bestes Projekt"),
//            ProjectStatus.LAUFEND,
//            new ProjectRequirement("PhD"),
//            new CreatorID(UUID.randomUUID()),
//            new CreatorName("Jann"),
//            new SupervisorName("Jann"),
//            modules);
//
//    this.projectRepository.save(project);
//    assertThat(this.projectRepository.findById(project.getId()).isPresent()).isEqualTo(true);
//  }
//}
