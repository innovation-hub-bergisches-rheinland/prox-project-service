package de.innovationhub.prox.projectservice.config;


import de.innovationhub.prox.projectservice.security.OrganizationRequestContextAuthorizationManager;
import de.innovationhub.prox.projectservice.security.ProjectRequestContextAuthorizationManager;
import de.innovationhub.prox.projectservice.security.UserRequestContextAuthorizationManager;
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

  private final ProjectRequestContextAuthorizationManager projectAuthorizationManager;
  private final UserRequestContextAuthorizationManager userAuthorizationManager;
  private final OrganizationRequestContextAuthorizationManager orgAuthorizationManager;

  private static final String[] PUBLIC_READ_PATHS = {
    "/projects/**",
    "/moduleTypes/**",
    "/studyPrograms/**",
    "/specializations/**",
    "/profile/**",
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**"
  };

  public SecurityConfig(ProjectRequestContextAuthorizationManager projectAuthorizationManager,
      UserRequestContextAuthorizationManager userAuthorizationManager,
      OrganizationRequestContextAuthorizationManager orgAuthorizationManager) {
    this.projectAuthorizationManager = projectAuthorizationManager;
    this.userAuthorizationManager = userAuthorizationManager;
    this.orgAuthorizationManager = orgAuthorizationManager;
  }

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
    //super.configure(http);
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeHttpRequests(registry ->
            registry
                .mvcMatchers(HttpMethod.GET, PUBLIC_READ_PATHS)
                .permitAll()
                .mvcMatchers(HttpMethod.HEAD, PUBLIC_READ_PATHS)
                .permitAll()
                .mvcMatchers(HttpMethod.OPTIONS, PUBLIC_READ_PATHS)
                .permitAll()
                // Don't allow the POST /projects endpoint as it is still exposed by Spring Data REST
                .mvcMatchers(HttpMethod.POST, "/projects/**")
                .denyAll()
                .mvcMatchers(HttpMethod.POST, "/users/{userId}/**")
                .access(userAuthorizationManager)
                .mvcMatchers(HttpMethod.POST, "/organizations/{orgId}/**")
                .access(orgAuthorizationManager)
                .mvcMatchers(HttpMethod.PUT, "/projects/{projectId}/**")
                .access(projectAuthorizationManager)
                .mvcMatchers(HttpMethod.PATCH, "/projects/{projectId}/**")
                .access(projectAuthorizationManager)
                .mvcMatchers(HttpMethod.DELETE, "/projects/{projectId}/**")
                .access(projectAuthorizationManager)
                .anyRequest()
                .denyAll()
        );
  }
}
