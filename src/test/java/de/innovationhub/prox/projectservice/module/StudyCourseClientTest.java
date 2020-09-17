package de.innovationhub.prox.projectservice.module;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.clazz.ValueObjectDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;

@SpringBootTest
@ContextConfiguration(classes = {StudyCourseClientTest.class})
class StudyCourseClientTest {
  private static final String UUID_REGEX = "[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}";
  private static final int WIREMOCK_PORT = 9123;

  @MockBean
  @Qualifier("eurekaClient")
  EurekaClient eurekaClient;

  StudyCourseClient studyCourseClient;

  WireMockServer wireMockServer;
  InstanceInfo instanceInfo;

  /**
   * Helper Method which reads a file
   *
   * @param filePath the resource location to resolve: either a "classpath:" pseudo URL, a "file:"
   *     URL, or a plain file path
   * @return the corresponding File read as String
   * @throws IOException if the resource cannot be resolved to a file in the file system
   */
  String readFromFile(String filePath) throws IOException {
    File file = ResourceUtils.getFile(filePath);
    return new String(Files.readAllBytes(file.toPath()));
  }

  @BeforeEach
  void setUp() throws IOException {
    // Initialize Mocks
    MockitoAnnotations.initMocks(StudyCourseClientTest.class);

    // Setup Wiremock Server
    this.wireMockServer =
        new WireMockServer(
            options().extensions(new ResponseTemplateTransformer(false)).port(WIREMOCK_PORT));

    // Configure Eureka Mock so that whenever an instance is requested the url points to the
    // Wiremock server
    this.instanceInfo = Mockito.mock(InstanceInfo.class);
    when(instanceInfo.getHomePageUrl()).thenReturn("http://localhost:" + WIREMOCK_PORT + "/");
    when(eurekaClient.getNextServerFromEureka(anyString(), anyBoolean())).thenReturn(instanceInfo);

    // Configure Wiremock Mappings
    this.wireMockServer.stubFor(
        get(urlPathEqualTo("/studyCourses"))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                    .withBody(readFromFile("classpath:module/StudyCourses.json"))
                    .withTransformers("response-template")));

    this.wireMockServer.stubFor(
        get(urlPathMatching("/studyCourses/" + UUID_REGEX + "/modules"))
            .willReturn(
                aResponse()
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE)
                    .withBody(readFromFile("classpath:module/StudyCourseModules.json"))
                    .withTransformers("response-template")));

    // Start Wiremock Server
    this.wireMockServer.start();

    // Initialize the StudyCourseClient with the mocked EurekaClient instance
    studyCourseClient = new StudyCourseClient(eurekaClient);
  }

  /**
   * generates the expected StudyCourseList which should be returned from method {{{@link
   * StudyCourseClient#getStudyCourses()}}} using the provided JSON-Response.
   *
   * <p>It is important to mention that the modules of each StudyCourse are equal because the test
   * uses the exact same JSON-Response for every StudyCourse when the modules are requested.
   *
   * @return List of StudyCourses
   * @throws MalformedURLException when a HATEOAS Link is invalid
   */
  List<StudyCourse> getExpectedStudyCourseList() throws MalformedURLException {

    final List<StudyCourse> expectedStudyCourseList = new ArrayList<>();

    StudyCourse wirtschaftsinformatik =
        new StudyCourse(new StudyCourseName("Wirtschaftsinformatik"), AcademicDegree.BACHELOR);
    Module bachelorArbeit =
        new Module(
            new ModuleName("Bachelor Arbeit "),
            ProjectType.BA); // Preserve space as it is equal to the json file
    bachelorArbeit.setExternalModuleID(
        new ExternalModuleID(
            new URL(
                instanceInfo.getHomePageUrl() + "modules/3bec3d62-b722-4273-b4a8-f115a69aa66e")));
    Module praxisProjekt = new Module(new ModuleName("Praxisprojekt"), ProjectType.PP);
    praxisProjekt.setExternalModuleID(
        new ExternalModuleID(
            new URL(
                instanceInfo.getHomePageUrl() + "modules/8e1e6673-b803-4376-807b-26e6e1311847")));
    wirtschaftsinformatik.setModules(
        new ArrayList<>() {
          {
            add(bachelorArbeit);
            add(praxisProjekt);
          }
        });
    wirtschaftsinformatik.setExternalStudyCourseID(
        new ExternalStudyCourseID(
            new URL(
                instanceInfo.getHomePageUrl()
                    + "studyCourses/2190d4e7-9336-4d65-b52f-bf1b83d0cfc4")));

    StudyCourse medieninformatik =
        new StudyCourse(new StudyCourseName("Medieninformatik"), AcademicDegree.MASTER);
    medieninformatik.setModules(
        new ArrayList<>() {
          {
            add(bachelorArbeit);
            add(praxisProjekt);
          }
        });
    medieninformatik.setExternalStudyCourseID(
        new ExternalStudyCourseID(
            new URL(
                instanceInfo.getHomePageUrl()
                    + "studyCourses/e6677573-1079-4501-bd42-d40c78937c12")));

    StudyCourse computerScience =
        new StudyCourse(new StudyCourseName("Informatik/Computer Science"), AcademicDegree.MASTER);
    computerScience.setModules(
        new ArrayList<>() {
          {
            add(bachelorArbeit);
            add(praxisProjekt);
          }
        });
    computerScience.setExternalStudyCourseID(
        new ExternalStudyCourseID(
            new URL(
                instanceInfo.getHomePageUrl()
                    + "studyCourses/a0530708-0073-4c3a-a9ad-480786926486")));

    expectedStudyCourseList.add(wirtschaftsinformatik);
    expectedStudyCourseList.add(medieninformatik);
    expectedStudyCourseList.add(computerScience);

    return expectedStudyCourseList;
  }

  @Test
  void when_get_study_courses_then_result_as_expected() throws Exception {
    List<StudyCourse> studyCourseList = studyCourseClient.getStudyCourses(); // actual result
    List<StudyCourse> expectedStudyCourseList = getExpectedStudyCourseList(); // expected result

    assertFalse(studyCourseList.isEmpty()); // actual should not be empty

    /* Create a JaVers instance which should compare the actual and expected result and check for
     * value equality.
     * Since StudyCourse and Module are Entities they would normally be compared by there identity
     * which is not applicable here because the id and so the identity is always different.
     * So they will be registered as ValueObjects and compared by their properties ignoring the
     * field "id".
     */
    Javers javers =
        JaversBuilder.javers()
            .registerValueObject(
                new ValueObjectDefinition(StudyCourse.class, Collections.singletonList("id")))
            .registerValueObject(
                new ValueObjectDefinition(Module.class, Collections.singletonList("id")))
            .build();
    Diff diff =
        javers.compareCollections(studyCourseList, expectedStudyCourseList, StudyCourse.class);

    assertEquals(0, diff.getChanges().size()); // expected result and actual result values are equal
  }
}
