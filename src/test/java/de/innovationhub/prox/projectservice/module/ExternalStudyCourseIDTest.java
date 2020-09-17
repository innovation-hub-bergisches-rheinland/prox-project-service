package de.innovationhub.prox.projectservice.module;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

class ExternalStudyCourseIDTest {

  @Test
  void testToString() throws URISyntaxException, MalformedURLException {
    String file = "/file?id=1234abc&name=test";
    String url = "http://example.org" + file;
    ExternalStudyCourseID externalStudyCourseID = new ExternalStudyCourseID(new URI(url).toURL());

    assertEquals(file, externalStudyCourseID.toString());
  }
}
