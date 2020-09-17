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

package de.innovationhub.prox.projectservice.module;

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

  private static final ProjectType[] filteredProjectTypes =
      new ProjectType[] {
        ProjectType.PP, ProjectType.BA, ProjectType.MA,
      };
  private final EurekaClient eurekaClient;

  public StudyCourseClient(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
  }

  private String serviceUrl() {
    InstanceInfo instance = this.eurekaClient.getNextServerFromEureka("module-service", false);
    return instance.getHomePageUrl() + "studyCourses";
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
          Link modulesLink = studyCourseResource.getLink("modules").orElseThrow();

          Traverson modulesTraverson = this.getTraversonInstance(modulesLink.getHref());

          final CollectionModel<EntityModel<Module>> moduleResources =
              modulesTraverson.follow("self").toObject(new CollectionModelType<>() {});

          for (EntityModel<Module> moduleResource : moduleResources.getContent()) {
            Module module = moduleResource.getContent();
            if (this.isModuleFiltered(module)) {
              module.setExternalModuleID(
                  new ExternalModuleID(
                      new URL(
                          moduleResource.getLink(IanaLinkRelations.SELF).orElseThrow().getHref())));
              studyCourse.addModule(module);
            }
          }

          studyCourse.setExternalStudyCourseID(
              new ExternalStudyCourseID(
                  new URL(
                      studyCourseResource
                          .getLink(IanaLinkRelations.SELF)
                          .orElseThrow()
                          .getHref())));
          studyCourses.add(studyCourse);
        }
      }
    } catch (Exception e) {
      StudyCourseClient.log.error("Error retrieving modules", e);
    }

    return studyCourses;
  }

  private boolean isModuleFiltered(Module module) {
    ProjectType moduleProjectType = module.getProjectType();

    for (ProjectType filteredProjectTypesList : StudyCourseClient.filteredProjectTypes) {

      if (moduleProjectType.compareTo(filteredProjectTypesList) == 0) {
        return true;
      }
    }

    return false;
  }
}
