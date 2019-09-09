package io.archilab.prox.projectservice.tags;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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
	
    private static final int MAX_LENGTH = 40;
	
	@Column(length = 40)
	private String tagName;
	
	public TagName(String tagName)
	{
		if (!TagName.isValid(tagName)) {
		      throw new IllegalArgumentException(
		          String.format("Name %s exceeded maximum number of %d allowed characters", tagName,
		        		  TagName.MAX_LENGTH));
		    }
		this.tagName = tagName.toLowerCase();
	}
	
	public static boolean isValid(String tagName) {
	    return tagName != null && tagName.length() <= TagName.MAX_LENGTH;
	  }

}
