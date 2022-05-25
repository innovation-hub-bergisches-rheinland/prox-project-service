package de.innovationhub.prox.projectservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class UserInfoRequestHeaderExtractorTest {

  private final UserInfoRequestHeaderExtractor extractor = new UserInfoRequestHeaderExtractor();

  @Test
  void shouldReturnNullWhenHeaderDoesNotExist() {
    var request = new MockHttpServletRequest();
    assertThat(extractor.parseUserInfoFromRequest(request)).isNull();
  }

  @Test
  void shouldReturnNullOnBlankHeader() {
    var request = new MockHttpServletRequest();
    request.addHeader("x-userinfo", "");
    assertThat(extractor.parseUserInfoFromRequest(request)).isNull();
  }

  @Test
  void shouldReturnNullOnInvalidJsonFormat() {
    var request = new MockHttpServletRequest();
    request.addHeader("x-userinfo", "{{}");
    assertThat(extractor.parseUserInfoFromRequest(request)).isNull();
  }

  @Test
  void shouldReturnNullOnInvalidMapping() {
    var request = new MockHttpServletRequest();
    request.addHeader("x-userinfo", """
        {
          "orgs": [],
          "sub": {},
          "subject": "12abc"
        """);
    assertThat(extractor.parseUserInfoFromRequest(request)).isNull();
  }

  @Test
  void shouldExtractUserInfo() {
    var request = new MockHttpServletRequest();
    var subject = UUID.randomUUID();
    var orgId1 = UUID.randomUUID();
    var orgId2 = UUID.randomUUID();
    request.addHeader("x-userinfo", """
        {
          "orgs": [ "%s", "%s" ],
          "sub": "%s"
        }
        """.formatted(orgId1, orgId2, subject));

    var result = extractor.parseUserInfoFromRequest(request);

    assertThat(result.sub()).isEqualTo(subject);
    assertThat(result.orgs()).containsExactlyInAnyOrder(orgId1, orgId2);
  }

  @Test
  void shouldIgnoreCasingOfHeader() {
    var request = new MockHttpServletRequest();
    var subject = UUID.randomUUID();
    var orgId1 = UUID.randomUUID();
    var orgId2 = UUID.randomUUID();
    request.addHeader("X-uSeRiNfO", """
        {
          "orgs": [ "%s", "%s" ],
          "sub": "%s"
        }
        """.formatted(orgId1, orgId2, subject));

    var result = extractor.parseUserInfoFromRequest(request);

    assertThat(result.sub()).isEqualTo(subject);
    assertThat(result.orgs()).containsExactlyInAnyOrder(orgId1, orgId2);
  }


}
