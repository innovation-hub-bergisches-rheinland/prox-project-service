package de.innovationhub.prox.projectservice.project;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.owners.user.User;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class ProjectJsonTest {

  @Autowired JacksonTester<Project> json;

  @Autowired ObjectMapper objectMapper;

  @Test
  void serialize() throws Exception {
    // Given
    var randomProject =
        new Project(
          "Test Project",
          "Test Project Description",
          "Test Project Short Description",
          "Test Project Requirement",
          ProjectStatus.AVAILABLE,
          "Test Project Creator Name",
          List.of(new Supervisor(UUID.randomUUID(), "Test Project Supervisor")),
          Collections.emptySet(),
          Collections.emptySet(),
          new User(UUID.randomUUID(), "Xavier Tester"),
          Instant.now(),
          Instant.now());

    // When
    var serialized = this.json.write(randomProject);

    // Then
    assertThat(serialized)
        .extractingJsonPathStringValue("@.id")
        .isEqualTo(randomProject.getId().toString());
    assertThat(serialized)
        .extractingJsonPathStringValue("@.name")
        .isEqualTo(randomProject.getName());
    assertThat(serialized)
        .extractingJsonPathStringValue("@.description")
        .isEqualTo(randomProject.getDescription());
    assertThat(serialized)
        .extractingJsonPathStringValue("@.shortDescription")
        .isEqualTo(randomProject.getShortDescription());
    assertThat(serialized)
        .extractingJsonPathStringValue("@.requirement")
        .isEqualTo(randomProject.getRequirement());
    assertThat(serialized)
        .extractingJsonPathStringValue("@.status")
        .isEqualTo(objectMapper.writeValueAsString(randomProject.getStatus()).replace("\"", ""));
    assertThat(serialized)
        .extractingJsonPathStringValue("@.creatorName")
        .isEqualTo(randomProject.getCreatorName());
    assertThat(serialized).extractingJsonPathArrayValue("@.supervisors").isNotEmpty();
    assertThat(serialized)
        .extractingJsonPathStringValue("@.owner.id")
        .isEqualTo(randomProject.getOwner().getId().toString());
    assertThat(serialized).extractingJsonPathStringValue("@.createdAt").isNotBlank();
    assertThat(serialized).extractingJsonPathStringValue("@.modifiedAt").isNotBlank();
  }

  @Test
  void deserialize() throws Exception {
    // Given
    var jsonStr =
        """
            {
              "id":"1c57e910-ac3c-4d72-8e11-961ef07cdf44",
              "name":"tg",
              "description":"eOMtThyhVNLWUZNRcBaQKxI",
              "shortDescription":"LRHCsQ",
              "requirement":"yedUsFwdkelQbxeTeQOvaScfqIOOmaa",
              "status":"FINISHED",
              "creatorName":"JxkyvRnL",
              "supervisors": [
                {
                  "id": "1c57e910-ac3c-4d72-8e11-961ef07cdf44",
                  "name": "tguuayKsvm"
                }
              ],
              "creatorID":"b921f1dd-3cbc-0495-fdab-8cd14d33f0aa",
              "createdAt":"2024-06-18T17:54:04.570+00:00",
              "modifiedAt":"2029-10-26T11:23:40.498+00:00"
            }
            """;

    // When
    var deserializedResult = this.json.parseObject(jsonStr);

    // Then
    assertThat(deserializedResult.getName()).isEqualTo("tg");
    assertThat(deserializedResult.getDescription()).isEqualTo("eOMtThyhVNLWUZNRcBaQKxI");
    assertThat(deserializedResult.getShortDescription()).isEqualTo("LRHCsQ");
    assertThat(deserializedResult.getRequirement()).isEqualTo("yedUsFwdkelQbxeTeQOvaScfqIOOmaa");
    assertThat(deserializedResult.getStatus()).isEqualTo(ProjectStatus.FINISHED);

    // Should not be deserialized
    assertThat(deserializedResult.getCreatedAt()).isNull();
    assertThat(deserializedResult.getModifiedAt()).isNull();
    
    assertThat(deserializedResult.getId())
        .isNotEqualByComparingTo(UUID.fromString("1c57e910-ac3c-4d72-8e11-961ef07cdf44"));
    assertThat(deserializedResult.getSupervisors())
        .containsExactlyInAnyOrder(
            new Supervisor(UUID.fromString("1c57e910-ac3c-4d72-8e11-961ef07cdf44"), "tguuayKsvm"));
  }
}
