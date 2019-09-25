package io.archilab.prox.projectservice.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.archilab.prox.projectservice.module.AcademicDegree;
import io.archilab.prox.projectservice.module.Module;
import io.archilab.prox.projectservice.module.ModuleName;
import io.archilab.prox.projectservice.module.ProjectType;
import io.archilab.prox.projectservice.module.StudyCourse;
import io.archilab.prox.projectservice.module.StudyCourseName;
import io.archilab.prox.projectservice.project.CreatorID;
import io.archilab.prox.projectservice.project.CreatorName;
import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectBigDataTestService;
import io.archilab.prox.projectservice.project.ProjectDescription;
import io.archilab.prox.projectservice.project.ProjectName;
import io.archilab.prox.projectservice.project.ProjectRequirement;
import io.archilab.prox.projectservice.project.ProjectShortDescription;
import io.archilab.prox.projectservice.project.ProjectStatus;
import io.archilab.prox.projectservice.project.SupervisorName;
import net.minidev.json.JSONObject;

@Component
@Profile("local-test-big-data")
public class BigTestDataSetupConfig implements ApplicationRunner {

  public final String words_lorem =
      "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet";

  public final int testAmount = 250;

  public final String tagServiceAddress;

  @Autowired
  private Environment env;


  public final String BACHELOR = "Bachelorarbeit";
  public final String PP = "PP";
  public final String MASTER = "Masterarbeit";

  private final Logger logger = LoggerFactory.getLogger(BigTestDataSetupConfig.class);

  @Autowired
  private ProjectBigDataTestService projectBigDataTestService;

  public BigTestDataSetupConfig(ProjectBigDataTestService projectBigDataTestService) {
    this.projectBigDataTestService = projectBigDataTestService;
    tagServiceAddress = env.getProperty("tag-service.address");
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {

    String[] split_words_lorem = words_lorem.split(" ");
    List<String> words = Arrays.asList(split_words_lorem);

    List<String> prof_words = Arrays.asList("Schmidt", "Heiner", "Klaus", "Peter", "Marie", "Heide",
        "Julian", "Bente", "Klocke", "Köhler", "Johannes", "Alken", "Felix", "Berg", "Bergstein",
        "Böhmer", "Lambers", "Lau", "Uria", "Lucia", "Iris", "Michael");

    Random rand = new Random();
    // add data for big data testing
    ArrayList<Project> test_projects = new ArrayList<Project>();

    Project newProject;

    List<Pair<UUID, String>> allCreatorIds = new ArrayList<Pair<UUID, String>>();
    for (int i = 0; i < testAmount / 3; i++) {
      String name = "Prof.";
      for (int k = 0; k < rand.nextInt(2) + 2; k++) {
        name += " " + prof_words.get(rand.nextInt(prof_words.size()));
      }
      allCreatorIds.add(Pair.of(UUID.randomUUID(), name));
    }

    ArrayList<String> allTags = getTagList();

    StudyCourse[] allStudyCourses = new StudyCourse[] {
        new StudyCourse(new StudyCourseName("Informatik Master"), AcademicDegree.MASTER),
        new StudyCourse(new StudyCourseName("TI Bachelor"), AcademicDegree.BACHELOR),
        new StudyCourse(new StudyCourseName("WI Bachelor"), AcademicDegree.BACHELOR),
        new StudyCourse(new StudyCourseName("Maschinenlehre"), AcademicDegree.BACHELOR),
        new StudyCourse(new StudyCourseName("Medien Master"), AcademicDegree.MASTER),
        new StudyCourse(new StudyCourseName("KI Master"), AcademicDegree.MASTER),
        new StudyCourse(new StudyCourseName("Data Science Master"), AcademicDegree.MASTER),
        new StudyCourse(new StudyCourseName("Chemie Master"), AcademicDegree.MASTER)};

    ArrayList<Module> allModules = new ArrayList<Module>();

    for (int i = 0; i < allStudyCourses.length; i++) {
      Module mod = null;
      StudyCourse studyCourse = allStudyCourses[i];
      if (studyCourse.getAcademicDegree().equals(AcademicDegree.BACHELOR)) {
        int count_modules = rand.nextInt(2);

        if (count_modules >= 0) {
          ProjectType projectType = ProjectType.BA;
          mod = new Module(new ModuleName(BACHELOR), projectType);
          mod.setStudyCourse(studyCourse);

          if (!allModules.contains(mod)) {
            studyCourse.addModule(mod);
            allModules.add(mod);
          }
        }
        if (count_modules == 1) {
          ProjectType projectType = ProjectType.PP;
          mod = new Module(new ModuleName(PP), projectType);
          mod.setStudyCourse(studyCourse);

          if (!allModules.contains(mod)) {
            studyCourse.addModule(mod);
            allModules.add(mod);
          }
        }

      } else {
        ProjectType projectType = ProjectType.MA;
        mod = new Module(new ModuleName(MASTER), projectType);
        mod.setStudyCourse(studyCourse);

        if (!allModules.contains(mod)) {
          studyCourse.addModule(mod);
          allModules.add(mod);
        }
      }
    }

    // save all Modules and Study Courses
    projectBigDataTestService.saveDataStudyCourses(allStudyCourses);
    projectBigDataTestService.saveDataModules(allModules);

    ProjectName projectName = null;
    ProjectShortDescription shortDescription = null;
    ProjectDescription description = null;
    ProjectStatus status = null;
    ProjectRequirement requirement = null;
    CreatorID creatorID = null;
    CreatorName creatorName = null;
    SupervisorName supervisorName = null;
    List<Module> modules = new ArrayList<Module>();
    String[] tags;

    for (int i = 0; i < testAmount; i++) {
      modules = new ArrayList<Module>();

      String projectNameString = stringOfWords(rand.nextInt(20) + 4, words, rand);
      String projectShortDescriptionString = stringOfWords(rand.nextInt(300) + 100, words, rand);
      String projectDescriptionAtring = stringOfWords(rand.nextInt(650) + 150, words, rand);
      String projectRequirementString = stringOfWords(rand.nextInt(30), words, rand);
      Pair<UUID, String> pair = allCreatorIds.get(rand.nextInt(allCreatorIds.size()));
      UUID creatorIDValue = pair.getFirst();
      String creatorNameString = pair.getSecond();
      String supervisorNameString = pair.getSecond();

      projectName = new ProjectName(projectNameString);
      shortDescription = new ProjectShortDescription(projectShortDescriptionString);
      description = new ProjectDescription(projectDescriptionAtring);
      status = ProjectStatus.values()[rand.nextInt(ProjectStatus.values().length)];
      requirement = new ProjectRequirement(projectRequirementString);
      creatorID = new CreatorID(creatorIDValue);
      creatorName = new CreatorName(creatorNameString);
      supervisorName = new SupervisorName(supervisorNameString);

      for (int k = 0; k < rand.nextInt(5) + 1; k++) {
        StudyCourse studyCourse = allStudyCourses[k];
        if (studyCourse.getAcademicDegree().equals(AcademicDegree.BACHELOR)
            && studyCourse.getModules().size() == 2) {
          for (int s = 0; s < rand.nextInt(2) + 1; s++) {
            Module module = studyCourse.getModules().get(s);

            if (!modules.contains(module)) {
              modules.add(module);
            }
          }
        } else {
          Module module = studyCourse.getModules().get(0);
          if (!modules.contains(module)) {
            modules.add(module);
          }
        }

      }

      newProject = new Project(projectName, shortDescription, description, status, requirement,
          creatorID, creatorName, supervisorName, modules);
      test_projects.add(newProject);

      // tags hinzufügen geht mit diesem desing nur über ein post auf den tag service.
      tags = new String[rand.nextInt(14)];
      for (int k = 0; k < tags.length; k++) {

        String link_tag = allTags.get(rand.nextInt(allTags.size()));
        boolean result = Arrays.stream(tags).anyMatch(link_tag::equals);
        if (!result) {
          tags[k] = link_tag;
        } else {
          k--;
          continue;
        }

      }
      putTags(tags, newProject.getId());

    }

    projectBigDataTestService.saveDataProjects(test_projects);

    logger.info("Done creating test data");

  }

  public void putTags(String[] links, UUID id) {

    final String url =
        "http://" + tagServiceAddress + ":9003/tagCollections/" + id.toString() + "/tags";

    HttpHeaders headers = new HttpHeaders();

    headers.setContentType(new MediaType("text", "uri-list"));

    RestTemplate restTemplate = new RestTemplate();

    String links_data = "";

    for (int i = 0; i < links.length; i++) {
      links_data += links[i];
      if (i < links.length - 1) {
        links_data += "\n";
      }
    }

    HttpEntity<String> entity = new HttpEntity<>(links_data, headers);

    // logger.info(url+" "+entity.toString());

    try {
      restTemplate.put(url, entity);

    } catch (Exception e) {
      e.printStackTrace();
      logger.error("error");
    }

  }

  private ArrayList<String> getTagList() {

    ArrayList<String> links = new ArrayList<String>();

    links.add(post("Informatik"));
    links.add(post("Mathematik"));
    links.add(post("DB1"));
    links.add(post("DB2"));
    links.add(post("GP"));
    links.add(post("UI"));
    links.add(post("Medien"));
    links.add(post("Technik"));
    links.add(post("Maschinen"));
    links.add(post("Strom"));
    links.add(post("BWL"));
    links.add(post("Erweiterre Strömungslehre"));
    links.add(post("Architektur"));
    links.add(post("Betirebssysteme"));
    links.add(post("Partner"));
    links.add(post("Extern"));
    links.add(post("Remote"));
    links.add(post("Linux"));
    links.add(post("Usability"));
    links.add(post("Design"));
    links.add(post("KI"));

    for (int i = 0; i < links.size(); i++) {
      logger.info(links.get(i));
    }

    return links;
  }

  public String post(String name) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    final String url = "http://" + tagServiceAddress + ":9003/tags";

    RestTemplate restTemplate = new RestTemplate();

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("tagName", name);

    HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, headers);

    String link = "";

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = null;
    logger.info("POST");
    try {
      String res = restTemplate.postForObject(url, entity, String.class);
      jsonNode = objectMapper.readTree(res);
      link = jsonNode.get("_links").get("self").get("href").asText();

      logger.info(link);

    } catch (Exception e) {
      logger.error("post");
      link = null;
    }

    if (link == null) {
      link = "";
      logger.info("Get");
      try {
        RestTemplate restTemplate2 = new RestTemplate();
        String res = restTemplate2.getForObject(
            url + "/search/findByTagName_TagName?tagName=" + name.toLowerCase(), String.class);
        jsonNode = objectMapper.readTree(res);
        // logger.info(jsonNode.toString());
        logger.info(jsonNode.get("_embedded").get("tags").elements().next().toString());
        logger
            .info(jsonNode.get("_embedded").get("tags").elements().next().get("_links").toString());
        logger.info(jsonNode.get("_embedded").get("tags").elements().next().get("_links")
            .get("self").toString());

        link = jsonNode.get("_embedded").get("tags").elements().next().get("_links").get("self")
            .get("href").asText();
        logger.info(link);
      } catch (Exception e) {
        logger.error("get");
      }
    }

    return link;

  }

  private String stringOfWords(int number, List<String> words, Random rand) {
    String string = "";

    for (int k = 0; k < number; k++) {
      if (k != 0) {
        string += " ";
      }
      string += words.get(rand.nextInt(words.size()));
    }

    return string;
  }

}
