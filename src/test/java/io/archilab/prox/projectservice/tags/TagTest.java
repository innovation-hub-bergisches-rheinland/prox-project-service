package io.archilab.prox.projectservice.tags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.archilab.prox.projectservice.module.Module;
import io.archilab.prox.projectservice.module.ModuleName;
import io.archilab.prox.projectservice.module.ModuleRepository;
import io.archilab.prox.projectservice.module.ProjectType;
import io.archilab.prox.projectservice.project.CreatorID;
import io.archilab.prox.projectservice.project.CreatorName;
import io.archilab.prox.projectservice.project.Project;
import io.archilab.prox.projectservice.project.ProjectDescription;
import io.archilab.prox.projectservice.project.ProjectName;
import io.archilab.prox.projectservice.project.ProjectRepository;
import io.archilab.prox.projectservice.project.ProjectRequirement;
import io.archilab.prox.projectservice.project.ProjectShortDescription;
import io.archilab.prox.projectservice.project.ProjectStatus;
import io.archilab.prox.projectservice.project.SupervisorName;
import io.micrometer.core.instrument.Tags;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TagTest {
	
	  @Autowired
	  ModuleRepository moduleRepository;

	  @Autowired
	  ProjectRepository projectRepository;
	  
	  @Autowired
	  TagRepository tagRepository;
	
	@Test
	public void tagNameConstraints()
	{
		String name_5="12345";
		String name_40="1234567890123456789012345678901234567890";
		String name_100="1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
		
		
		TagName tagName_5_a = new TagName(name_5);
		TagName tagName_5_b = new TagName(name_5);
		
		assertEquals(tagName_5_a,tagName_5_b);
		assertEquals(tagName_5_a.getTagName(),name_5);
		
		TagName tagName_40 = new TagName(name_40);
		
		assertEquals(tagName_40.getTagName(),name_40);
		
		try 
		{
			TagName tagName_100 = new TagName(name_100);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			 assertThat(e.getMessage(), containsString("exceeded maximum number of"));
		}
	}
	
	@Test
	public void tagCreate()
	{
		String name_5="12345";
		String name_40="12345678901234567890";
		
		TagName tagName_5 = new TagName(name_5);
		TagName tagName_40 = new TagName(name_40);
		
		Tag tag_5 = new Tag(tagName_5);
		Tag tag_40 = new Tag(tagName_40);
		
		assertEquals(tag_5.getTagName().getTagName(),name_5);
		assertEquals(tag_40.getTagName().getTagName(),name_40);	
		
	}
	@Test
	public void addTags()
	{
	    List<Module> modules = new ArrayList<>();
	    modules.add(new Module(new ModuleName("Module 1"),ProjectType.PP));
	    modules.add(new Module(new ModuleName("Module 2"),ProjectType.MA));
	    modules.add(new Module(new ModuleName("Module 3"),ProjectType.BA));
	    this.moduleRepository.saveAll(modules);
	    assertThat(this.moduleRepository.count()).isEqualTo(3);
	    
	    List<Tag> tags1 = new ArrayList<>();
	    List<Tag> tags2 = new ArrayList<>();
	    
	    String tagName = "tag1";
	    
	    Tag tag1 = new Tag(new TagName(tagName));
	    Tag tag2 = new Tag(new TagName("tag2"));
	    Tag tag3 = new Tag(new TagName("tag3"));
	    Tag tag4 = new Tag(new TagName("tag4"));
	    Tag tag5 = new Tag(new TagName("tag5"));
	    tags1.add(tag1);
	    tags1.add(tag2);
	    tags1.add(tag3);
	    
	    tags2.add(tag3);
	    tags2.add(tag4);
	    tags2.add(tag5);
	    
	    tagRepository.saveAll(tags1);
	    tagRepository.saveAll(tags2);
	    
	    Optional<Tag> foundTag =tagRepository.findByTagName_TagNameEquals( (tagName));
	    Set<Tag> allTags =tagRepository.findByTagName_TagNameContaining(("tag"));
	    
	    if(foundTag.isPresent())
	    {
	    	assertEquals(foundTag.get().getId(), tag1.getId());
	    }
	    else { fail(); }
	    
	    if(!allTags.isEmpty())
	    {
	    	assertEquals(allTags.size(),5);
	    }
	    else { fail(); }
	    
	    
	    Project project1 = new Project(new ProjectName("Projekt1"),
	    		new ProjectShortDescription("mini inhalt"), 
	    		new ProjectDescription("Projekt1"), 
	    		ProjectStatus.LAUFEND, 
	    		 new ProjectRequirement("ALl done"), 
	    		 new CreatorID(UUID.randomUUID()), 
	    		 new CreatorName("ein Name"), 
	    		 new SupervisorName("Name2"), 
	    		modules,
	    		tags1);  
	    Project project2 = new Project(new ProjectName("Projekt2"),
	    		new ProjectShortDescription("mini inhalt"), 
	    		new ProjectDescription("Projekt1"), 
	    		ProjectStatus.LAUFEND, 
	    		 new ProjectRequirement("ALl done"), 
	    		 new CreatorID(UUID.randomUUID()), 
	    		 new CreatorName("ein Name"), 
	    		 new SupervisorName("Name2"), 
	    		modules,
	    		tags2);  

	    this.projectRepository.save(project1);
	    this.projectRepository.save(project2);
	    
	    assertEquals(project1.getTags().get(0), tag1);
	    assertEquals(project1.getTags().get(1), tag2);
	    assertEquals(project1.getTags().get(2), tag3);
	    
	    assertEquals(project2.getTags().get(0), tag3);
	    assertEquals(project2.getTags().get(1), tag4);
	    assertEquals(project2.getTags().get(2), tag5);
	    
	    Optional<Project> o_project1 = this.projectRepository.findById(project1.getId());
	    Optional<Project> o_project2 = this.projectRepository.findById(project2.getId());

	    project1 = o_project1.get();
	    project2 = o_project2.get();
	    
	    assertEquals(project1.getTags().get(0), tag1);
	    assertEquals(project1.getTags().get(1), tag2);
	    assertEquals(project1.getTags().get(2), tag3);
	    
	    assertEquals(project2.getTags().get(0), tag3);
	    assertEquals(project2.getTags().get(1), tag4);
	    assertEquals(project2.getTags().get(2), tag5);
	    
	}
	

}
