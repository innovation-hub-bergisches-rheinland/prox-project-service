package io.archilab.prox.projectservice.utils;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.repository.CrudRepository;

public interface AuthenticationUtils {
  Optional<UUID> getUserUUIDFromRequest(HttpServletRequest request);
}