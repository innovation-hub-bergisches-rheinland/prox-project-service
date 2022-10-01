package de.innovationhub.prox.projectservice.project;

import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProjectTagListener {
  private final ProjectRepository projectRepository;

  public ProjectTagListener(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @KafkaListener(topics = "event.item.tagged", groupId = "project-tag-listener")
  public void tagProject(ItemTaggedDto event) {
    var optionalProject = projectRepository.findById(event.item());
    if(optionalProject.isEmpty()) {
      // OK, we don't care
      return;
    }
    var proposal = optionalProject.get();
    proposal.setTags(event.tags().stream().map(ItemTaggedDto.Tag::tag).toList());
    projectRepository.save(proposal);
  }
}
