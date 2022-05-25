package de.innovationhub.prox.projectservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.lang.Nullable;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserInfoRequestHeaderExtractor {
  private final ObjectMapper objectMapper = new ObjectMapper();

  public @Nullable UserInfo parseUserInfoFromRequest(HttpServletRequest request) {
    var header = request.getHeader("x-userinfo");
    if (header == null) {
      return null;
    }
    try {
      return objectMapper.readValue(header, UserInfo.class);
    } catch (JsonMappingException e) {
      log.warn("Could not map the X-UserInfo header onto defined data structure", e);
      return null;
    } catch (JsonProcessingException e) {
      log.warn("X-UserInfo header does not contain a valid JSON payload", e);
      return null;
    }
  }
}
