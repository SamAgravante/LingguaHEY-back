package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.lingguahey.Entity.StoryEntity;

public interface StoryRepository extends JpaRepository<StoryEntity, Integer>{

}
