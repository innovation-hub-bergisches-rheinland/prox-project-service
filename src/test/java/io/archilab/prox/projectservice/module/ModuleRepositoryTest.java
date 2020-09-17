package io.archilab.prox.projectservice.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.archilab.prox.projectservice.project.CreatorID;
import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectName;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ModuleRepositoryTest {

  @Autowired
  ModuleRepository moduleRepository;

  static List<Module> sampleModules = new ArrayList<>();
  static Module sampleModule;

  @BeforeAll
  static void createSampleModules() {
    sampleModule = new Module(new ModuleName("Module 1"), ProjectType.UNDEFINED);
    sampleModules.add(sampleModule);
    sampleModules.add(new Module(new ModuleName("Module 2"), ProjectType.UNDEFINED));
    sampleModules.add(new Module(new ModuleName("Module 3"), ProjectType.UNDEFINED));
  }

  void saveSampleModule() {
    moduleRepository.save(sampleModule);

    Optional<Module> optionalModule = moduleRepository.findById(sampleModule.getId());
    assertTrue(optionalModule.isPresent());
    Module foundModule = optionalModule.get();
    assertEquals(sampleModule, foundModule);
  }

  @Test
  void when_project_saved_then_found_and_equal() {
    saveSampleModule();
  }

  @Test
  void when_project_updated_then_found_and_equal() {
    saveSampleModule();

    //Copy module to maintain integrity of sampleModule
    Module copiedModule = sampleModule;

    copiedModule.setName(new ModuleName("Changed Module Name"));
    copiedModule.setProjectType(ProjectType.BA);

    moduleRepository.save(copiedModule);
    Optional<Module> optionalModuleFound = moduleRepository.findById(copiedModule.getId());
    assertTrue(optionalModuleFound.isPresent());
    Module foundModule = optionalModuleFound.get();
    assertEquals(copiedModule, foundModule);
  }

  @Test
  void when_project_deleted_then_not_found() {
    saveSampleModule();

    moduleRepository.delete(sampleModule);

    Optional<Module> optionalModule = moduleRepository.findById(sampleModule.getId());
    assertFalse(optionalModule.isPresent());
  }

  @Test
  void when_find_by_external_module_id_is_valid_then_found()
      throws URISyntaxException, MalformedURLException {
    sampleModule.setExternalModuleID(new ExternalModuleID(new URI("http://example.org").toURL()));
    moduleRepository.save(sampleModule);
    Optional<Module> foundModule = moduleRepository.findById(sampleModule.getId());

    assertTrue(foundModule.isPresent());
    assertEquals(sampleModule, foundModule.get());

    foundModule = moduleRepository.findByExternalModuleID(sampleModule.getExternalModuleID());

    assertTrue(foundModule.isPresent());
    assertEquals(sampleModule, foundModule.get());
  }
}
