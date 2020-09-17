package io.archilab.prox.projectservice.project;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

class ProjectRepositoryCustomImplTest extends ProjectRepositoryTest {

  @Test
  void when_projects_saved_then_find_all_by_ids_find_all() {
    projectRepository.saveAll(sampleProjects);
    UUID[] uuids = new UUID[sampleProjects.size()];
    for(int i = 0; i < sampleProjects.size(); i++) {
      uuids[i] = sampleProjects.get(i).getId();
    }

    List<Project> foundProjects = projectRepository.findAllByIds(uuids);

    assertEquals(sampleProjects.size(), foundProjects.size());
  }
}