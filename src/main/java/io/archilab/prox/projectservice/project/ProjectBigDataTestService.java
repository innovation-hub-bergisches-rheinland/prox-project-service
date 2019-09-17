package io.archilab.prox.projectservice.project;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.archilab.prox.projectservice.module.Module;
import io.archilab.prox.projectservice.module.ModuleRepository;
import io.archilab.prox.projectservice.module.StudyCourse;
import io.archilab.prox.projectservice.module.StudyCourseRepository;

@Service
@Transactional
public class ProjectBigDataTestService {
	
  private final Logger logger = LoggerFactory.getLogger(ProjectBigDataTestService.class);
	
  private final ProjectRepository projectRepository;

  private final ModuleRepository moduleRepository;

  private final StudyCourseRepository studyCourseRepository;


  
  public ProjectBigDataTestService(  ProjectRepository projectRepository, ModuleRepository moduleRepository, StudyCourseRepository studyCourseRepository) {
    this.projectRepository = projectRepository;
    this.moduleRepository = moduleRepository;
    this.studyCourseRepository = studyCourseRepository;

  }
  
  public void saveDataProjects(ArrayList<Project> test_projects)
  {
  	projectRepository.saveAll(test_projects);
  }
  
  public void saveDataModules(ArrayList<Module> test_modules)
  {
  	moduleRepository.saveAll(test_modules);
  }
  


	public void saveDataStudyCourses(StudyCourse[] allStudyCourses) {
		for (int i = 0; i < allStudyCourses.length; i++) {

	  	studyCourseRepository.save(allStudyCourses[i]);
		}
		
	}

}
