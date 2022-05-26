package de.innovationhub.prox.projectservice.security;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import java.util.UUID;

public record UserInfo(@JsonProperty("sub") UUID sub, @JsonProperty("orgs") Set<UUID> orgs) {}
