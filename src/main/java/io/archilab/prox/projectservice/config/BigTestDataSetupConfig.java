package io.archilab.prox.projectservice.config;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import io.archilab.prox.projectservice.module.AcademicDegree;
import io.archilab.prox.projectservice.module.Module;
import io.archilab.prox.projectservice.module.ModuleName;
import io.archilab.prox.projectservice.module.ModuleRepository;
import io.archilab.prox.projectservice.module.ProjectType;
import io.archilab.prox.projectservice.module.StudyCourse;
import io.archilab.prox.projectservice.module.StudyCourseName;
import io.archilab.prox.projectservice.module.StudyCourseService;
import io.archilab.prox.projectservice.project.CreatorID;
import io.archilab.prox.projectservice.project.CreatorName;
import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectBigDataTestService;
import io.archilab.prox.projectservice.project.ProjectDescription;
import io.archilab.prox.projectservice.project.ProjectName;
import io.archilab.prox.projectservice.project.ProjectRepository;
import io.archilab.prox.projectservice.project.ProjectRequirement;
import io.archilab.prox.projectservice.project.ProjectShortDescription;
import io.archilab.prox.projectservice.project.ProjectStatus;
import io.archilab.prox.projectservice.project.SupervisorName;
import io.archilab.prox.projectservice.tags.Tag;
import io.archilab.prox.projectservice.tags.TagName;

@Component
@Profile("local-test-big-data")
public class BigTestDataSetupConfig implements ApplicationRunner   {


  private final Logger logger = LoggerFactory.getLogger(BigTestDataSetupConfig.class);

  @Autowired
  private ProjectBigDataTestService projectBigDataTestService;
  
  public BigTestDataSetupConfig(  ProjectBigDataTestService projectBigDataTestService) {
    this.projectBigDataTestService = projectBigDataTestService;
  }
  

  @Override
  public void run(ApplicationArguments  args) throws Exception 
  {
  	final int testAmount = 10000;
  	Random rand = new Random();
  	// add data for big data testing
  	ArrayList<Project> test_projects = new ArrayList<Project>();
  	
  	Project newProject;
  	
    List<Pair<UUID,String>> allCreatorIds = new ArrayList<Pair<UUID,String>>();
    for(int i=0;i<testAmount/3;i++)
    {
      int length = 85;
      boolean useLetters = true;
      boolean useNumbers = false;
      String name = RandomStringUtils.random(length, useLetters, useNumbers);
    	
    	allCreatorIds.add(Pair.of(UUID.randomUUID(),name));
    }
    
    ArrayList<Tag> allTags = new ArrayList<Tag>();
    for(int i=0;i<testAmount/8;i++)
    {
      int length = 40;
      boolean useLetters = true;
      boolean useNumbers = false;
      String name = RandomStringUtils.random(length, useLetters, useNumbers);
    	Tag tag = new Tag(new TagName(name));
      if(!allTags.contains(tag))
      {
      	allTags.add(tag);
      } 
    }
    projectBigDataTestService.saveDataTags(allTags);
    
    StudyCourse[] allStudyCourses = new StudyCourse[] {
    		new StudyCourse(new StudyCourseName(RandomStringUtils.random(26, true, false)), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName(RandomStringUtils.random(26, true, false)), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName(RandomStringUtils.random(26, true, false)), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName(RandomStringUtils.random(26, true, false)), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName(RandomStringUtils.random(26, true, false)), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName(RandomStringUtils.random(26, true, false)), AcademicDegree.values()[rand.nextInt(2)])
    };
    
    
    ArrayList<Module> allModules = new ArrayList<Module>();
    for(int i=0;i<testAmount/8;i++)
    {
      boolean useLetters = true;
      boolean useNumbers = false;
      ProjectType projectType = ProjectType.values()[rand.nextInt(3)];
      Module mod = new Module(new ModuleName(RandomStringUtils.random(22, useLetters, useNumbers)), projectType);
      StudyCourse studyCourse = allStudyCourses[rand.nextInt(allStudyCourses.length)];
      mod.setStudyCourse(studyCourse);
      if(!allModules.contains(mod))
      {
      	studyCourse.addModule(mod);
      	allModules.add(mod);
      } 
    }
    // save all Modules and Study Courses
    projectBigDataTestService.saveDataStudyCourses(allStudyCourses);
  	projectBigDataTestService.saveDataModules(allModules);
    
    
  	ProjectName projectName = null;
  	ProjectShortDescription shortDescription = null;
  	ProjectDescription description = null;
  	ProjectStatus status = null;
  	ProjectRequirement requirement = null;
  	CreatorID creatorID = null;
  	CreatorName creatorName = null;
  	SupervisorName supervisorName = null;
  	List<Module> modules = new ArrayList<Module>();
  	List<Tag> tags  = new ArrayList<Tag>();
  	
  	for(int i=0;i< testAmount;i++)
  	{
    	modules.clear();
    	tags.clear();
    	
    	String projectNameString = RandomStringUtils.random(rand.nextInt(120), true, true);
    	String projectShortDescriptionString = RandomStringUtils.random(rand.nextInt(3000), true, true);
    	String projectDescriptionAtring = RandomStringUtils.random(rand.nextInt(6000), true, true);
    	String projectRequirementString = RandomStringUtils.random(rand.nextInt(80), true, true);
    	Pair<UUID, String> pair = allCreatorIds.get(rand.nextInt(allCreatorIds.size()));
      UUID creatorIDValue = pair.getFirst();
      String creatorNameString = pair.getSecond();
      String supervisorNameString = pair.getSecond();
    	
    	projectName = new ProjectName(projectNameString);
    	shortDescription = new ProjectShortDescription(projectShortDescriptionString);   	
    	description = new ProjectDescription(projectDescriptionAtring);
    	status = ProjectStatus.values()[rand.nextInt( ProjectStatus.values().length)];
    	requirement = new ProjectRequirement(projectRequirementString);
    	creatorID = new CreatorID(creatorIDValue);
    	creatorName = new CreatorName(creatorNameString);
    	supervisorName = new SupervisorName(supervisorNameString);
    	
      for(int k=0;k < rand.nextInt(33); k++)
      {
      	Tag tag = allTags.get(k);
      	if(!tags.contains(tag))
      	{
      		tags.add(tag);
      	}  	
      }
      
      for(int k=0;k < rand.nextInt(33); k++)
      {
      	Module module = allModules.get(k);
      	if(!allModules.contains(module))
      	{
      		modules.add(module);
      	}  	
      }
    	
    	newProject = new Project(projectName,shortDescription,description,status,requirement,creatorID,creatorName,supervisorName,modules,tags);
    	test_projects.add(newProject);
  	}
  	
  	projectBigDataTestService.saveDataProjects(test_projects);
  	
  	logger.info("Done creating test data");
  	
  }
  
}
