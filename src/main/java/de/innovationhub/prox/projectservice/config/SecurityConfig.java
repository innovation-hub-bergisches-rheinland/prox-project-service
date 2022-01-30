package de.innovationhub.prox.projectservice.config;


import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

  private static final String STUDY_PROGRAM_PATTERN = "/studyPrograms/**";
  private static final String MODULE_TYPE_PATTERN = "/moduleTypes/**";
  private static final String PROJECTS_PATTERN = "/projects/**";
  private static final String PROJECT_STUDY_COURSES_PATTERN = "/projectStudyCourses/**";
  private static final String PROJECTS_ID_PATTERN = "/projects/{id}/**";
  private static final String PROJECT_MODULES_PATTERN = "/projectModules/**";
  private static final String PROJPROJECT_STUDY_COURSESECTS_PATTERN =
      "/projprojectStudyCoursesects/**";
  private static final String PROFILE_PATTERN = "/profile/**";
  public static final String[] SWAGGER_PATHS = {
    "/swagger-resources/**", "/swagger-ui/**", "/swagger-ui/", "/v2/api-docs", "/v3/api-docs"
  };

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider =
        this.keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, SecurityConfig.PROJECTS_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.HEAD, SecurityConfig.PROJECTS_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, SecurityConfig.PROJECTS_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.POST, SecurityConfig.PROJECTS_PATTERN)
        .access("hasAnyRole('professor', 'company-manager')")
        .antMatchers(HttpMethod.GET, SecurityConfig.PROJECTS_ID_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.HEAD, SecurityConfig.PROJECTS_ID_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, SecurityConfig.PROJECTS_ID_PATTERN)
        .permitAll()
        .antMatchers(SecurityConfig.PROJECTS_ID_PATTERN)
        .access(
            "hasAnyRole('professor', 'company-manager') and hasPermission(#id, 'Project', 'WRITE')")
        .antMatchers(HttpMethod.GET, SecurityConfig.PROJECT_STUDY_COURSES_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.HEAD, SecurityConfig.PROJECT_STUDY_COURSES_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, SecurityConfig.PROJPROJECT_STUDY_COURSESECTS_PATTERN)
        .permitAll()
        .antMatchers(SecurityConfig.PROJECT_STUDY_COURSES_PATTERN)
        .denyAll()
        .antMatchers(HttpMethod.GET, SecurityConfig.PROJECT_MODULES_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.HEAD, SecurityConfig.PROJECT_MODULES_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, SecurityConfig.PROJECT_MODULES_PATTERN)
        .permitAll()
        .antMatchers(SecurityConfig.PROJECT_MODULES_PATTERN)
        .denyAll()
        .antMatchers(SecurityConfig.PROFILE_PATTERN)
        .permitAll()
        .antMatchers(HttpMethod.GET, SecurityConfig.SWAGGER_PATHS)
        .permitAll()
        .antMatchers(SecurityConfig.MODULE_TYPE_PATTERN)
        .permitAll()
        .antMatchers(SecurityConfig.STUDY_PROGRAM_PATTERN)
        .permitAll()
        .anyRequest()
        .denyAll();
  }
}
