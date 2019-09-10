package io.archilab.prox.projectservice.tags;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class TagRecommendationController {

  @Autowired
  private TagCounterRepository tagCounterRepository;

  @Autowired
  private TagRepository tagRepository;


  @RequestMapping(path = "/tags/{id}/recommendations", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> tagRecommendations(@PathVariable("id") UUID tagId) {
    List<Tag> recommendedTags = getRecommendedTags(tagId);
    if (recommendedTags == null) {
      return ResponseEntity.notFound().build();
    }

    // Create Resources
    Resources<Tag> recommendedTagResources = new Resources<>(recommendedTags);
    recommendedTagResources.add(ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(TagRecommendationController.class).tagRecommendations(tagId))
        .withSelfRel());

    for (Tag recommendedTag : recommendedTags) {
      // resources.add(ControllerLinkBuilder.linkTo()); TODO add links to tags
    }

    return ResponseEntity.ok(recommendedTagResources);
  }

  private List<Tag> getRecommendedTags(UUID tagId) {
    Optional<Tag> optionalSearchTag = tagRepository.findById(tagId);
    if (!optionalSearchTag.isPresent()) {
      return null;
    }

    // Add Recommended Tags
    List<Tag> recommendedTags = new ArrayList<>();
    Tag searchTag = optionalSearchTag.get();
    for (TagCounter tagCounter : tagCounterRepository.findByTag1OrTag2OrderByCountDesc(searchTag,
        searchTag)) {
      recommendedTags.add(tagCounter.getOtherTag(searchTag));
    }

    // Add Remaining Tags
    for (Tag tag : tagRepository.findAll()) {
      if (!recommendedTags.contains(tag)) {
        recommendedTags.add(tag);
      }
    }

    return recommendedTags;
  }
}
