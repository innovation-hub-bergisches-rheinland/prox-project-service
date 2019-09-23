package io.archilab.prox.projectservice.config;

import java.net.MalformedURLException;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.archilab.prox.projectservice.project.Project;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

  @Autowired
  private EntityManager entityManager;

  private final Logger log = LoggerFactory.getLogger(RestConfig.class);

  // @Autowired
  private Environment env;

  private final EurekaClient eurekaClient;

  private String portLokal = "";

  public RestConfig(EurekaClient eurekaClient, Environment env) {
    this.eurekaClient = eurekaClient;
    this.env = env;

    portLokal = env.getProperty("tagServiceLink.port");

    // InstanceInfo instance = serviceUrl("tag-service");
    // portLokal = ""+instance.getPort();
    // log.info(portLokal);
  }

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.exposeIdsFor(this.entityManager.getMetamodel().getEntities().stream()
        .map(Type::getJavaType).toArray(Class[]::new));
  }



  @Bean
  public ResourceProcessor<Resource<Project>> personProcessor(HttpServletRequest request) {

    return new ResourceProcessor<Resource<Project>>() {

      @Override
      public Resource<Project> process(Resource<Project> resource) {

        String projectID = resource.getContent().getId().toString();
        // log.info(request.toString());

        UriComponents request = ServletUriComponentsBuilder.fromCurrentRequest().build();
        String scheme = request.getScheme() + "://";
        String serverName = request.getHost();
        String serverPort = "";
        // serverPort = (request.getPort() == 80) ? "" : ":" + request.getPort();

        if (request.getPort() == 8081) {
          serverPort = ":" + request.getPort();
        } else if (serverName.contains("localhost")) {
          serverPort = ":" + portLokal;
        }

        resource.add(new Link(
            scheme + serverName + serverPort + "/"
                + env.getProperty("tagServiceLink.tag-collection") + "/" + projectID + "/tags",
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
