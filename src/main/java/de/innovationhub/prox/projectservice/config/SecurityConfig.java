package de.innovationhub.prox.projectservice.config;


import de.innovationhub.prox.projectservice.security.OrganizationRequestContextAuthorizationManager;
import de.innovationhub.prox.projectservice.security.ProjectRequestContextAuthorizationManager;
import de.innovationhub.prox.projectservice.security.ProposalRequestContextAuthorizationManager;
import de.innovationhub.prox.projectservice.security.UserRequestContextAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {

  private final ProjectRequestContextAuthorizationManager projectAuthorizationManager;
  private final ProposalRequestContextAuthorizationManager proposalAuthorizationManager;
  private final UserRequestContextAuthorizationManager userAuthorizationManager;
  private final OrganizationRequestContextAuthorizationManager orgAuthorizationManager;

  private static final String[] PUBLIC_READ_PATHS = {
    "/projects/**",
    "/proposals/**",
    "/users/**",
    "/organizations/**",
    "/modules/**",
    "/studyPrograms/**",
    "/specializations/**",
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**"
  };
  private static final String PROJECT_WRITE_PATH =
      "/projects/{%s}/**".formatted(ProjectRequestContextAuthorizationManager.PROJECT_ID_VARIABLE);
  private static final String PROPOSAL_WRITE_PATH =
      "/proposals/{%s}/**"
          .formatted(ProposalRequestContextAuthorizationManager.PROPOSAL_ID_VARIABLE);

  public SecurityConfig(
      ProjectRequestContextAuthorizationManager projectAuthorizationManager,
      ProposalRequestContextAuthorizationManager proposalAuthorizationManager,
      UserRequestContextAuthorizationManager userAuthorizationManager,
      OrganizationRequestContextAuthorizationManager orgAuthorizationManager) {
    this.projectAuthorizationManager = projectAuthorizationManager;
    this.proposalAuthorizationManager = proposalAuthorizationManager;
    this.userAuthorizationManager = userAuthorizationManager;
    this.orgAuthorizationManager = orgAuthorizationManager;
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthoritiesConverter());
    jwtConverter.setPrincipalClaimName("sub");
    return jwtConverter;
  }

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()))
        .authorizeHttpRequests(
            registry ->
                registry
                    .mvcMatchers(HttpMethod.GET, PUBLIC_READ_PATHS)
                    .permitAll()
                    .mvcMatchers(HttpMethod.HEAD, PUBLIC_READ_PATHS)
                    .permitAll()
                    .mvcMatchers(HttpMethod.OPTIONS, PUBLIC_READ_PATHS)
                    .permitAll()
                    .mvcMatchers("/user/**")
                    .authenticated()
                    .mvcMatchers(HttpMethod.POST, "/users/{userId}/**")
                    .access(userAuthorizationManager)
                    .mvcMatchers(HttpMethod.POST, "/organizations/{orgId}/**")
                    .access(orgAuthorizationManager)
                    .mvcMatchers(HttpMethod.PUT, PROJECT_WRITE_PATH)
                    .access(projectAuthorizationManager)
                    .mvcMatchers(HttpMethod.PATCH, PROJECT_WRITE_PATH)
                    .access(projectAuthorizationManager)
                  .mvcMatchers(HttpMethod.DELETE, PROJECT_WRITE_PATH)
                  .access(projectAuthorizationManager)
                  .mvcMatchers(HttpMethod.POST, "/proposals/{proposalId}/commitment")
                  .hasRole("professor")
                  .mvcMatchers(HttpMethod.PUT, PROPOSAL_WRITE_PATH)
                  .access(proposalAuthorizationManager)
                  .mvcMatchers(HttpMethod.PATCH, PROPOSAL_WRITE_PATH)
                  .access(proposalAuthorizationManager)
                  .mvcMatchers(HttpMethod.DELETE, PROPOSAL_WRITE_PATH)
                  .access(proposalAuthorizationManager)
                  .mvcMatchers(HttpMethod.POST, "/projects/reconciliation")
                  .hasRole("admin")
                  .mvcMatchers(HttpMethod.POST, "/proposals/reconciliation")
                  .hasRole("admin")
                    .anyRequest()
                    .denyAll());

    return http.build();
  }
}
