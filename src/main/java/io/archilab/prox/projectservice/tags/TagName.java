package io.archilab.prox.projectservice.tags;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import io.archilab.prox.projectservice.project.ProjectDescription;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagName {
	
	public String tagName;
	
	public TagName(String tagName)
	{
		this.tagName = tagName;
	}

}
