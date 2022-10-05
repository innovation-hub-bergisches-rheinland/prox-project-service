package de.innovationhub.prox.projectservice.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectTagListener {
  private final ProjectRepository projectRepository;
  private final ObjectMapper objectMapper;

  public ProjectTagListener(ProjectRepository projectRepository, ObjectMapper objectMapper) {
    this.projectRepository = projectRepository;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "event.item.tagged", groupId = "project-tag-listener")
  public void tagProject(ConsumerRecord<String, String> event) {

    ItemTaggedDto parsedEvent;
    try {
      parsedEvent = this.objectMapper.readValue(event.value(), ItemTaggedDto.class);
    } catch (JsonProcessingException e) {
      log.error("Could not parse event", e);
      throw new RuntimeException(e);
    }
    var optionalProject = projectRepository.findById(parsedEvent.id());
    if(optionalProject.isEmpty()) {
      // OK, we don't care
      return;
    }
    var project = optionalProject.get();
    project.setTags(parsedEvent.tags().stream().toList());
    projectRepository.save(project);
  }
}
