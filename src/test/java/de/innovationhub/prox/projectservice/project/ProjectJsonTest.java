package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class ProjectJsonTest {
  @Autowired
  JacksonTester<Project> json;

  @Test
  void serialize() throws Exception {
    // Given
    var easyRandom = new EasyRandom();
    var randomProject = easyRandom.nextObject(Project.class);

    // When
    var serialized = this.json.write(randomProject);

    // Then
    assertThat(serialized).extractingJsonPathStringValue("@.id").isEqualTo(randomProject.getId().toString());
    assertThat(serialized).extractingJsonPathStringValue("@.name").isEqualTo(randomProject.getName());
    assertThat(serialized).extractingJsonPathStringValue("@.description").isEqualTo(randomProject.getDescription());
    assertThat(serialized).extractingJsonPathStringValue("@.shortDescription").isEqualTo(randomProject.getShortDescription());
    assertThat(serialized).extractingJsonPathStringValue("@.requirement").isEqualTo(randomProject.getRequirement());
    assertThat(serialized).extractingJsonPathStringValue("@.status").isEqualTo(randomProject.getStatus().toString());
    assertThat(serialized).extractingJsonPathStringValue("@.context").isEqualTo(randomProject.getContext().toString());
    assertThat(serialized).extractingJsonPathStringValue("@.creatorName").isEqualTo(randomProject.getCreatorName());
    assertThat(serialized).extractingJsonPathStringValue("@.supervisorName").isEqualTo(randomProject.getSupervisorName());
    assertThat(serialized).extractingJsonPathStringValue("@.creatorID").isEqualTo(randomProject.getCreatorID().toString());
    assertThat(serialized).extractingJsonPathStringValue("@.created").isNotBlank();
    assertThat(serialized).extractingJsonPathStringValue("@.modified").isNotBlank();
  }

  @Test
  void deserialize() throws Exception {
    // Given
    var jsonStr = """
        {
          "id":"1c57e910-ac3c-4d72-8e11-961ef07cdf44",
          "name":"tg",
          "description":"eOMtThyhVNLWUZNRcBaQKxI",
          "shortDescription":"LRHCsQ",
          "requirement":"yedUsFwdkelQbxeTeQOvaScfqIOOmaa",
          "status":"ABGESCHLOSSEN",
          "context":"PROFESSOR",
          "creatorName":"JxkyvRnL",
          "supervisorName":"tguuayKsvm",
          "creatorID":"b921f1dd-3cbc-0495-fdab-8cd14d33f0aa",
          "created":"2024-06-18T17:54:04.570+00:00",
          "modified":"2029-10-26T11:23:40.498+00:00"
        }
        """;

    // When
    var deserializedResult = this.json.parseObject(jsonStr);

    // Then
    assertThat(deserializedResult.getName()).isEqualTo("tg");
    assertThat(deserializedResult.getDescription()).isEqualTo("eOMtThyhVNLWUZNRcBaQKxI");
    assertThat(deserializedResult.getShortDescription()).isEqualTo("LRHCsQ");
    assertThat(deserializedResult.getRequirement()).isEqualTo("yedUsFwdkelQbxeTeQOvaScfqIOOmaa");
    assertThat(deserializedResult.getStatus()).isEqualTo(ProjectStatus.ABGESCHLOSSEN);
    assertThat(deserializedResult.getContext()).isEqualTo(ProjectContext.PROFESSOR);

    // Should not be deserialized
    assertThat(deserializedResult.getCreatorName()).isNullOrEmpty();
    assertThat(deserializedResult.getCreated()).isNull();
    assertThat(deserializedResult.getModified()).isNull();
    assertThat(deserializedResult.getId()).isNotEqualByComparingTo(UUID.fromString("1c57e910-ac3c-4d72-8e11-961ef07cdf44"));
  }
}
