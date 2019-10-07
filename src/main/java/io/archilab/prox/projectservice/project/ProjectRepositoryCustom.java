package io.archilab.prox.projectservice.project;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface ProjectRepositoryCustom {

  List<Project> findAllByIds(@RequestParam("projectIds") UUID[] projectIds);
}
