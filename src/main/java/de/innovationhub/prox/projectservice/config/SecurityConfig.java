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
        .mvcMatchers(HttpMethod.GET, PUBLIC_READ_PATHS)
        .permitAll()
        .mvcMatchers(HttpMethod.HEAD, PUBLIC_READ_PATHS)
        .permitAll()
        .mvcMatchers(HttpMethod.OPTIONS, PUBLIC_READ_PATHS)
        .permitAll()
        .mvcMatchers(HttpMethod.POST, "/projects/**")
        .access("hasAnyRole('professor', 'company-manager')")
        .mvcMatchers(HttpMethod.PUT, "/projects/{id}/**")
        .access(
            "hasAnyRole('professor', 'company-manager') and hasPermission(#id, 'Project', 'WRITE')")
        .mvcMatchers(HttpMethod.PATCH, "/projects/{id}/**")
        .access(
            "hasAnyRole('professor', 'company-manager') and hasPermission(#id, 'Project', 'WRITE')")
        .mvcMatchers(HttpMethod.DELETE, "/projects/{id}/**")
        .access(
            "hasAnyRole('professor', 'company-manager') and hasPermission(#id, 'Project', 'WRITE')")
        .anyRequest()
        .denyAll();
  }
}
