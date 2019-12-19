package io.archilab.prox.projectservice.module;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.server.core.TypeReferences.CollectionModelType;
import org.springframework.hateoas.server.core.TypeReferences.PagedModelType;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StudyCourseClient {

  private static final String[] filteredModuleNames =
      new String[] {"Master Thesis", "Masterarbeit", "Bachelor", "Praxisprojekt"};
  private final EurekaClient eurekaClient;

  public StudyCourseClient(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
  }

  private String serviceUrl() {
    InstanceInfo instance = this.eurekaClient.getNextServerFromEureka("module-service", false);
    String url = instance.getHomePageUrl() + "studyCourses";
    return url;
  }

  private Traverson getTraversonInstance(String url) {
    URI uri;
    try {
      uri = new URI(url);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Eureka provided an invalid service URL", e);
    }

    return new Traverson(uri, MediaTypes.HAL_JSON);
  }

  public List<StudyCourse> getStudyCourses() {
    Traverson traverson = this.getTraversonInstance(this.serviceUrl());

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

        final PagedModel<EntityModel<StudyCourse>> pagedStudyCourseResources =
            traverson
                .follow("self")
                .withTemplateParameters(params)
                .toObject(new PagedModelType<>() {});

        reachedLastPage =
            (++currentPage >= pagedStudyCourseResources.getMetadata().getTotalPages());

        for (EntityModel<StudyCourse> studyCourseResource :
            pagedStudyCourseResources.getContent()) {
          StudyCourse studyCourse = studyCourseResource.getContent();
          Link modulesLink = studyCourseResource.getLink("modules").get();

          Traverson modulesTraverson = this.getTraversonInstance(modulesLink.getHref());
          if (traverson == null) {
            continue;
          }

          final CollectionModel<EntityModel<Module>> moduleResources =
              modulesTraverson.follow("self").toObject(new CollectionModelType<>() {});

          for (EntityModel<Module> moduleResource : moduleResources.getContent()) {
            Module module = moduleResource.getContent();
            if (this.isModuleFiltered(module)) {
              module.setExternalModuleID(
                  new ExternalModuleID(
                      new URL(moduleResource.getLink(IanaLinkRelations.SELF).get().getHref())));
              studyCourse.addModule(module);
            }
          }

          studyCourse.setExternalStudyCourseID(
              new ExternalStudyCourseID(
                  new URL(studyCourseResource.getLink(IanaLinkRelations.SELF).get().getHref())));
          studyCourses.add(studyCourse);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      StudyCourseClient.log.error("Error retrieving modules");
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
