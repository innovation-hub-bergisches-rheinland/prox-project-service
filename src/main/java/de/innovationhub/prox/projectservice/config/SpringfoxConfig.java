package de.innovationhub.prox.projectservice.config;

import com.google.common.base.Predicate;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringfoxConfig {

  private static final String[] PREDICATE_NAMES = {
      "saveStudyCourse",
      "deleteStudyCourse",
      "studyCourseModules",
      "saveModule",
      "deleteModule",
      "moduleStudyCourse"
  };

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
        .forCodeGeneration(true)
        .select()
        .paths(PathSelectors.ant("/projects/**")
            .or(PathSelectors.ant("/projectStudyCourses/**"))
            .or(PathSelectors.ant("/projectModules/**")))
        .apis(customRequestHandlers())
        .build();
  }

  //TODO Remove when Controllers are implemented and use Swagger/Springfox annotations instead
  private java.util.function.Predicate<RequestHandler> customRequestHandlers() {
    return new Predicate<>() {
      @Override
      public boolean apply(@Nullable RequestHandler input) {
        System.out.println(input.getName());
        System.out.println(input);
        if(ArrayUtils.contains(PREDICATE_NAMES, input.getName())) {
          Set<RequestMethod> methodSet = input.supportedMethods();
          return methodSet.contains(RequestMethod.GET)
              || methodSet.contains(RequestMethod.HEAD)
              || methodSet.contains(RequestMethod.OPTIONS);
        }
        return true;
      }
    };
  }
}
