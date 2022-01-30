package de.innovationhub.prox.projectservice.config;


import de.innovationhub.prox.projectservice.project.Project;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

  private final EntityManager entityManager;
  private final LocalValidatorFactoryBean validator;

  @Autowired
  public RestConfig(EntityManager entityManager, LocalValidatorFactoryBean validator) {
    this.entityManager = entityManager;
    this.validator = validator;
  }

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
  public RepresentationModelProcessor<EntityModel<Project>> projectProcessor(
      HttpServletRequest request) {
    // Note: This cannot be refactored to a lambda expression for whatever reason
    //  probably because of some generic type shenanigans
    return new RepresentationModelProcessor<>() {

      @Override
      public EntityModel<Project> process(EntityModel<Project> resource) {
        String projectID = resource.getContent().getId().toString();
        String creatorID = resource.getContent().getCreatorID().toString();

        resource.add(
            Link.of(
                request.getRequestURL() + "/tagCollections/" + projectID + "/tags",
                "tagCollection"));
        resource.add(
            Link.of(request.getRequestURL() + "/professors/" + creatorID,
                "professor"));
        return resource;
      }
    };
  }
}
