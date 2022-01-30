package de.innovationhub.prox.projectservice.config;


import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringfoxConfig {

  private static final String[] PREDICATE_NAMES = {
    "saveStudyProgram",
    "deleteStudyProgram",
    "studyProgramModuleTypes",
    "saveModuleType",
    "deleteModuleType",
    "moduleTypeStudyPrograms",
    "studyProgramModules"
  };

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
        .forCodeGeneration(true)
        .securitySchemes(Collections.singletonList(jwtScheme()))
        .securityContexts(Collections.singletonList(securityContext()))
        .groupName("project-service")
        .select()
        .paths(
            PathSelectors.ant("/projects/**")
                .or(PathSelectors.ant("/moduleTypes/**"))
                .or(PathSelectors.ant("/studyPrograms/**")))
        .apis(customRequestHandlers())
        .build();
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(List.of(defaultAuth()))
        .operationSelector(o -> o.httpMethod() != HttpMethod.GET)
        .build();
  }

  private SecurityReference defaultAuth() {
    return SecurityReference.builder().scopes(new AuthorizationScope[0]).reference("JWT").build();
  }

  private SecurityScheme jwtScheme() {
    return HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("JWT").build();
  }

  // TODO Remove when Controllers are implemented and use Swagger/Springfox annotations instead
  private Predicate<RequestHandler> customRequestHandlers() {
    return input -> {
      if (input != null
          && input.getName() != null
          && ArrayUtils.contains(PREDICATE_NAMES, input.getName())) {
        Set<RequestMethod> methodSet = input.supportedMethods();
        return methodSet.contains(RequestMethod.GET)
            || methodSet.contains(RequestMethod.HEAD)
            || methodSet.contains(RequestMethod.OPTIONS);
      }
      return true;
    };
  }
}
