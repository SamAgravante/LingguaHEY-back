package edu.cit.alibata.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.cit.alibata.Entity.StoryEntity;
import edu.cit.alibata.Repository.StoryRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class StoryService {

    @Autowired
    private StoryRepository storyRepo;

    // Create a new StoryEntity
    public StoryEntity postStoryEntity(StoryEntity story) {
        return storyRepo.save(story);
    }

    // Retrieve all StoryEntities
    public List<StoryEntity> getAllStoryEntity() {
        return storyRepo.findAll();
    }

    // Retrieve a single StoryEntity by id
    public StoryEntity getStoryEntity(int storyId) {
        return storyRepo.findById(storyId).get();
    }

    // Update an existing StoryEntity
    public StoryEntity putStoryEntity(int storyId, StoryEntity newStory) {
        try {
        StoryEntity story = storyRepo.findById(storyId).get();
        story.setTitle(newStory.getTitle());
        story.setStoryText(newStory.getStoryText());
        story.setYoutubeVideoId(newStory.getYoutubeVideoId());
        story.setCompleted(newStory.isCompleted());
        return storyRepo.save(story);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Activity " + storyId + " not found!");
        }
    }

    // Delete a StoryEntity by id
    @SuppressWarnings("unused")
    public String deleteStoryEntity(int storyId) {
        if (storyRepo.findById(storyId) != null) {
            storyRepo.deleteById(storyId);
            return "Story " + storyId + " deleted successfully!";
        } else {
            return "Story " + storyId + " not found!";
        }
    }
}

