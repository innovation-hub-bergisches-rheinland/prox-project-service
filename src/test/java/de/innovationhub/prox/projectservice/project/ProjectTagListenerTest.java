package de.innovationhub.prox.projectservice.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.projectservice.tag.event.ItemTaggedDto;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProjectTagListenerTest {
  ProjectRepository projectRepository = mock(ProjectRepository.class);

  ProjectTagListener projectTagListener = new ProjectTagListener(projectRepository);

  @Test
  void shouldSkipWhenNoProjectIsFound() {
    when(projectRepository.findById(any())).thenReturn(Optional.empty());

    projectTagListener.tagProject(new ItemTaggedDto(UUID.randomUUID(), Set.of()));

    // No exception
  }

  @Test
  void shouldAddTags() {
    var project = new Project();
    when(projectRepository.findById(any())).thenReturn(Optional.of(project));

    projectTagListener.tagProject(new ItemTaggedDto(project.getId(), Set.of(new ItemTaggedDto.Tag("tag1"), new ItemTaggedDto.Tag("tag2"))));

    assert project.getTags().contains("tag1");
    assert project.getTags().contains("tag2");
  }
}
