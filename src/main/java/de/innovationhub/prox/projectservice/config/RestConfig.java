package de.innovationhub.prox.projectservice.config;


import de.innovationhub.prox.projectservice.project.Project;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

// TODO Refactor: Better use eureka
@Configuration
public class RestConfig implements RepositoryRestConfigurer {

  private final EntityManager entityManager;
  private Environment env;
  private String tagServicePort = "";
  private String professorServicePort = "";

  public RestConfig(Environment env, EntityManager entityManager) {
    this.env = env;
    this.tagServicePort = env.getProperty("tagServiceLink.port");
    this.professorServicePort = env.getProperty("professorServiceLink.port");
    this.entityManager = entityManager;
  }

  @Autowired private LocalValidatorFactoryBean validator;

  @Override
  public void configureValidatingRepositoryEventListener(
      ValidatingRepositoryEventListener validatingListener) {
    validatingListener.addValidator("beforeCreate", validator);
    validatingListener.addValidator("beforeSave", validator);
  }

  @Override
  public void configureRepositoryRestConfiguration(
      RepositoryRestConfiguration config, CorsRegistry cors) {
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
        String creatorID = resource.getContent().getCreatorID().toString();

        UriComponents request = ServletUriComponentsBuilder.fromCurrentRequest().build();
        String scheme = request.getScheme() + "://";
        String serverName = request.getHost();
        String tagServicePort2 = "";
        String professorServicePot = "";

        if (request.getPort() == 8081) {
          tagServicePort2 = ":" + request.getPort();
          professorServicePot = ":" + request.getPort();
        } else if (request.getPort() == 9002) {
          tagServicePort2 = ":" + RestConfig.this.tagServicePort;
          professorServicePot = ":" + RestConfig.this.professorServicePort;
        }

        resource.add(
            Link.of(
                scheme
                    + serverName
                    + tagServicePort2
                    + "/"
                    + RestConfig.this.env.getProperty("tagServiceLink.tag-collection")
                    + "/"
                    + projectID
                    + "/tags",
                "tagCollection"));
        resource.add(
            Link.of(
                scheme
                    + serverName
                    + professorServicePot
                    + "/"
                    + RestConfig.this.env.getProperty("professorServiceLink.professor-resource")
                    + "/"
                    + creatorID,
                "professor"));
        return resource;
      }
    };
  }
}
