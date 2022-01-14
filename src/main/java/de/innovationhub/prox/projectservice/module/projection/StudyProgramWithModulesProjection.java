package de.innovationhub.prox.projectservice.module.projection;

import de.innovationhub.prox.projectservice.module.ModuleType;
import de.innovationhub.prox.projectservice.module.StudyProgram;
import java.util.Set;
import org.springframework.data.rest.core.config.Projection;

public interface StudyProgramWithModulesProjection {
  String getKey();
  String getName();
  Set<ModuleType> getModules();
}
