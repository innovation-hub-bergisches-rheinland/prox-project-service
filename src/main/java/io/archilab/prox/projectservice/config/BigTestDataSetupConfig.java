package io.archilab.prox.projectservice.config;


import java.util.ArrayList;
import java.util.Arrays;
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
	
	public final String words_lorem = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet";

	public final int testAmount = 10000;

  private final Logger logger = LoggerFactory.getLogger(BigTestDataSetupConfig.class);

  @Autowired
  private ProjectBigDataTestService projectBigDataTestService;
  
  public BigTestDataSetupConfig(  ProjectBigDataTestService projectBigDataTestService) {
    this.projectBigDataTestService = projectBigDataTestService;
  }
  

  @Override
  public void run(ApplicationArguments  args) throws Exception 
  {
  	String[] split_words_lorem = words_lorem.split(" ");
  	List<String> words = Arrays.asList(split_words_lorem);
  	
  	Random rand = new Random();
  	// add data for big data testing
  	ArrayList<Project> test_projects = new ArrayList<Project>();
  	
  	Project newProject;
  	
    List<Pair<UUID,String>> allCreatorIds = new ArrayList<Pair<UUID,String>>();
    for(int i=0;i<testAmount/3;i++)
    {
    	String name = "";
    	for(int k=0;k<rand.nextInt(5)+2;k++)
    	{
    		if(k!=0)
    		{
    			name+=" ";
    		}
    		name+= words.get(rand.nextInt(words.size()));
    	}   	
    	allCreatorIds.add(Pair.of(UUID.randomUUID(),name));
    }
    
    ArrayList<Tag> allTags = new ArrayList<Tag>();
    allTags.add(new Tag(new TagName("Informatik")));
    allTags.add(new Tag(new TagName("Mathematik")));
    allTags.add(new Tag(new TagName("DB1")));
    allTags.add(new Tag(new TagName("DB2")));
    allTags.add(new Tag(new TagName("GP")));
    allTags.add(new Tag(new TagName("Wirtschaft")));
    allTags.add(new Tag(new TagName("UI")));
    allTags.add(new Tag(new TagName("Medien")));
    allTags.add(new Tag(new TagName("Technik")));
    allTags.add(new Tag(new TagName("Maschinen")));
    allTags.add(new Tag(new TagName("Strom")));
    allTags.add(new Tag(new TagName("BWL")));
    allTags.add(new Tag(new TagName("Erweiterre Strömungslehre")));
    allTags.add(new Tag(new TagName("Architektur")));
    allTags.add(new Tag(new TagName("Betirebssysteme")));
    allTags.add(new Tag(new TagName("Partner")));
    allTags.add(new Tag(new TagName("Extern")));
    allTags.add(new Tag(new TagName("Remote")));
       
    projectBigDataTestService.saveDataTags(allTags);
    
    StudyCourse[] allStudyCourses = new StudyCourse[] {
    		new StudyCourse(new StudyCourseName("Informatik Master"), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName("TI Bachelor"), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName("WI Bachelor"), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName("Maschinenlehre"), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName("Medien Master"), AcademicDegree.values()[rand.nextInt(2)]),
    		new StudyCourse(new StudyCourseName("Chemie Master"), AcademicDegree.values()[rand.nextInt(2)])
    };
    
    
    ArrayList<Module> allModules = new ArrayList<Module>();
    for(int i=0;i<testAmount/8;i++)
    {
    	String name = "";
    	for(int k=0;k<rand.nextInt(2)+2;k++)
    	{
    		if(k!=0)
    		{
    			name+=" ";
    		}
    		name+= words.get(rand.nextInt(words.size()));
    	} 
      ProjectType projectType = ProjectType.values()[rand.nextInt(3)+1];
      Module mod = new Module(new ModuleName(name), projectType);
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
    	
    	
    	
    	String projectNameString = stringOfWords(rand.nextInt(20),words,rand);
    	String projectShortDescriptionString = stringOfWords(rand.nextInt(500),words,rand);
    	String projectDescriptionAtring = stringOfWords(rand.nextInt(800),words,rand);
    	String projectRequirementString = stringOfWords(rand.nextInt(60),words,rand);
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
    	
      for(int k=0;k < rand.nextInt(allTags.size()); k++)
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
  
  private String stringOfWords(int number, List<String> words, Random rand)
  {
  	String string="";
  	
  	for(int k=0;k<number;k++)
  	{
  		if(k!=0)
  		{
  			string+=" ";
  		}
  		string+= words.get(rand.nextInt(words.size()));
  	} 
  	
  	return string;
  }
  
}
