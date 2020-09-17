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

package de.innovationhub.prox.projectservice.config;

import de.innovationhub.prox.projectservice.project.Project;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

  private final EntityManager entityManager;
  private Environment env;
  private String portLokal = "";

  public RestConfig(Environment env, EntityManager entityManager) {
    this.env = env;
    this.portLokal = env.getProperty("tagServiceLink.port");
    this.entityManager = entityManager;
  }

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.exposeIdsFor(
        this.entityManager.getMetamodel().getEntities().stream()
            .map(Type::getJavaType)
            .toArray(Class[]::new));
  }

  @Bean
  public RepresentationModelProcessor<EntityModel<Project>> personProcessor(
      HttpServletRequest request) {

    return new RepresentationModelProcessor<>() {

      @Override
      public EntityModel<Project> process(EntityModel<Project> resource) {

        String projectID = resource.getContent().getId().toString();

        UriComponents request = ServletUriComponentsBuilder.fromCurrentRequest().build();
        String scheme = request.getScheme() + "://";
        String serverName = request.getHost();
        String serverPort = "";

        if (request.getPort() == 8081) {
          serverPort = ":" + request.getPort();
        } else if (request.getPort() == 9002) {
          serverPort = ":" + RestConfig.this.portLokal;
        }

        resource.add(
            new Link(
                scheme
                    + serverName
                    + serverPort
                    + "/"
                    + RestConfig.this.env.getProperty("tagServiceLink.tag-collection")
                    + "/"
                    + projectID
                    + "/tags",
                "tagCollection"));
        return resource;
      }
    };
  }
}
