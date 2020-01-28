package io.archilab.prox.projectservice.project;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProjectRepositoryCustom {

  List<Project> findAllByIds(@RequestParam("projectIds") UUID[] projectIds);
}
