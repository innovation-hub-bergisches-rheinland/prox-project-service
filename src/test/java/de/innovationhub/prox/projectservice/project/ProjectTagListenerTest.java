package de.innovationhub.prox.projectservice.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

class ProjectTagListenerTest {
  ProjectRepository projectRepository = mock(ProjectRepository.class);
  ObjectMapper objectMapper = new ObjectMapper();

  ProjectTagListener projectTagListener = new ProjectTagListener(projectRepository, objectMapper);

  @Test
  void shouldSkipWhenNoProjectIsFound() throws JsonProcessingException {
    when(projectRepository.findById(any())).thenReturn(Optional.empty());
    var event = new ItemTaggedDto(UUID.randomUUID(), Set.of());
    var record = new ConsumerRecord<String, String>("event.item.tagged", 0, 0, null, objectMapper.writeValueAsString(event));

    projectTagListener.tagProject(record);

    // No exception
  }

  @Test
  void shouldAddTags() throws JsonProcessingException {
    var project = new Project();
    when(projectRepository.findById(any())).thenReturn(Optional.of(project));

    var event = new ItemTaggedDto(project.getId(), Set.of(new ItemTaggedDto.Tag("tag1"), new ItemTaggedDto.Tag("tag2")));
    var record = new ConsumerRecord<String, String>("event.item.tagged", 0, 0, null, objectMapper.writeValueAsString(event));

    projectTagListener.tagProject(record);

    assert project.getTags().contains("tag1");
    assert project.getTags().contains("tag2");
  }
}
