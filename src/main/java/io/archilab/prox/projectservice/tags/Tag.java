package io.archilab.prox.projectservice.tags;

import javax.persistence.Entity;

import io.archilab.prox.projectservice.core.AbstractEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends AbstractEntity
{
	public TagName tagName;
	
	public Tag(TagName tagName)
	{
		this.tagName = tagName;
	}
	
	
}
