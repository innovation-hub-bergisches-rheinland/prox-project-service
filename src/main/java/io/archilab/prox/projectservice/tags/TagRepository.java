package io.archilab.prox.projectservice.tags;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface TagRepository  extends PagingAndSortingRepository<Tag, UUID>{
	
	  Optional<Tag> findByTagName_TagNameEquals(@Param(value = "tagName") String tagName);

	  Set<Tag> findByTagName_TagNameContaining(
	      @Param(value = "tagName") String tagName);

}
