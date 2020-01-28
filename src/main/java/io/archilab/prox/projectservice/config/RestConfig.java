package io.archilab.prox.projectservice.config;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.archilab.prox.projectservice.project.Project;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

  private final EurekaClient eurekaClient;
  @Autowired private EntityManager entityManager;
  // @Autowired
  private Environment env;
  private String portLokal = "";

  public RestConfig(EurekaClient eurekaClient, Environment env) {
    this.eurekaClient = eurekaClient;
    this.env = env;
    this.portLokal = env.getProperty("tagServiceLink.port");
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

  private InstanceInfo serviceUrl(String service) {
    InstanceInfo instance = this.eurekaClient.getNextServerFromEureka(service, false);

    return instance;
  }
}
