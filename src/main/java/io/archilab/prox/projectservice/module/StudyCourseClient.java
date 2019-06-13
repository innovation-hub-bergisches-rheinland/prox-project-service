package io.archilab.prox.projectservice.module;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.stereotype.Component;


@Component
public class StudyCourseClient {

  private static final String[] filteredModuleNames =
      new String[] {"Master Thesis", "Masterarbeit", "Bachelor", "Praxisprojekt"};

  private final Logger logger = LoggerFactory.getLogger(StudyCourseClient.class);

  @Value("${module.service.url}")
  private String moduleServiceURL;


  public StudyCourseClient() {}

  private Traverson getTraversonInstance(String url) {
    try {
      return new Traverson(new URI(url), MediaTypes.HAL_JSON);
    } catch (URISyntaxException e) {
      this.logger.error("Could not init Traverson");
      e.printStackTrace();
      return null;
    }
  }

  public List<StudyCourse> getStudyCourses() {
    Traverson traverson = this.getTraversonInstance(this.moduleServiceURL);
    if (traverson == null) {
      return new ArrayList<>();
    }

    List<StudyCourse> studyCourses = new ArrayList<>();

    try {
      int currentPage = 0;
      boolean reachedLastPage = false;

      while (!reachedLastPage) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", currentPage);

        final PagedResources<Resource<StudyCourse>> pagedStudyCourseResources =
            traverson.follow("self").withTemplateParameters(params)
                .toObject(new TypeReferences.PagedResourcesType<Resource<StudyCourse>>() {});

        reachedLastPage =
            (++currentPage >= pagedStudyCourseResources.getMetadata().getTotalPages());

        for (Resource<StudyCourse> studyCourseResource : pagedStudyCourseResources.getContent()) {
          StudyCourse studyCourse = studyCourseResource.getContent();
          Link modulesLink = studyCourseResource.getLink("modules");

          Traverson modulesTraverson = this.getTraversonInstance(modulesLink.getHref());
          if (traverson == null) {
            continue;
          }

          final Resources<Resource<Module>> moduleResources = modulesTraverson.follow("self")
              .toObject(new TypeReferences.ResourcesType<Resource<Module>>() {});

          for (Resource<Module> moduleResource : moduleResources.getContent()) {
            Module module = moduleResource.getContent();
            if (this.isModuleFiltered(module)) {
              module.setExternalModuleID(
                  new ExternalModuleID(new URL(moduleResource.getId().getHref())));
              studyCourse.addModule(module);
            }
          }

          studyCourse.setExternalStudyCourseID(
              new ExternalStudyCourseID(new URL(studyCourseResource.getId().getHref())));
          studyCourses.add(studyCourse);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.logger.error("Error retrieving modules");
    }

    return studyCourses;
  }

  private boolean isModuleFiltered(Module module) {
    String moduleName = module.getName().getName();

    for (String filteredModuleName : StudyCourseClient.filteredModuleNames) {
      if (moduleName.toLowerCase().contains(filteredModuleName.toLowerCase())) {
        return true;
      }
    }

    return false;
  }

  /*
   * public List<StudyCourse> getStudyCourses() { // Test-method which creates studyCourses try {
   * List<StudyCourse> studyCourses = new ArrayList<>();
   * 
   * int coursesC = 50; int modulesC = 10; int moduleID = 0; for (int i = 0; i < coursesC; i++) {
   * StudyCourse sc = new StudyCourse(); sc.setAcademicDegree(AcademicDegree.MASTER);
   * sc.setExternalStudyCourseID(new ExternalStudyCourseID(new
   * URL("http://localhost:9002/studyCourses/" + i))); sc.setName(new StudyCourseName("Studiengang "
   * + i));
   * 
   * List<Module> modules = new ArrayList<>(); for (int j = 0; j < modulesC; j++) { Module module =
   * new Module(); module.setExternalModuleID(new ExternalModuleID(new
   * URL("http://localhost:9002/modules/" + moduleID))); module.setName(new ModuleName("Modul " +
   * moduleID)); moduleID++; modules.add(module); } sc.setModules(modules); studyCourses.add(sc); }
   * 
   * return studyCourses; } catch (Exception e) { return new ArrayList<>(); } }
   */
}
