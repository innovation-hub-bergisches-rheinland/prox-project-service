package de.innovationhub.prox.projectservice.config;


import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.Specialization;
import de.innovationhub.prox.projectservice.module.StudyProgram;
import de.innovationhub.prox.projectservice.owners.user.User;
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
import org.springframework.http.HttpMethod;
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

    // Specialization is read-only
    config
        .getExposureConfiguration()
        .forDomainType(Specialization.class)
        .withCollectionExposure(
            (metadata, httpMethods) ->
                httpMethods.disable(
                    HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.POST, HttpMethod.PATCH))
        .withItemExposure(
            (metadata, httpMethods) ->
                httpMethods.disable(
                    HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.POST, HttpMethod.PATCH));

    // ModuleType is read-only
    config
        .getExposureConfiguration()
        .forDomainType(ModuleType.class)
        .withCollectionExposure(
            (metadata, httpMethods) ->
                httpMethods.disable(
                    HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.POST, HttpMethod.PATCH))
        .withItemExposure(
            (metadata, httpMethods) ->
                httpMethods.disable(
                    HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.POST, HttpMethod.PATCH));

    // StudyProgram is read-only
    config
        .getExposureConfiguration()
        .forDomainType(StudyProgram.class)
        .withCollectionExposure(
            (metadata, httpMethods) ->
                httpMethods.disable(
                    HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.POST, HttpMethod.PATCH))
        .withItemExposure(
            (metadata, httpMethods) ->
                httpMethods.disable(
                    HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.POST, HttpMethod.PATCH));
  }

  @Bean
  public RepresentationModelProcessor<EntityModel<Project>> projectProcessor(
      HttpServletRequest request) {
    // Note: This cannot be refactored to a lambda expression for whatever reason
    //  probably because of some generic type shenanigans
    return new RepresentationModelProcessor<>() {

      @Override
      public EntityModel<Project> process(EntityModel<Project> resource) {
        var project = resource.getContent();
        String projectID = project.getId().toString();

        resource.add(
            Link.of(
                request.getRequestURL() + "/tagCollections/" + projectID + "/tags",
                "tagCollection"));
        if(project.getOwner() instanceof User) {
          String creatorID = project.getOwner().getId().toString();
          resource.add(Link.of(request.getRequestURL() + "/users/" + creatorID, "owner"));
        }
        return resource;
      }
    };
  }
}
