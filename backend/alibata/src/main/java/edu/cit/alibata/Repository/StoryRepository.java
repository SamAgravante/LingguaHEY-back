package edu.cit.alibata.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.alibata.Entity.StoryEntity;

public interface StoryRepository extends JpaRepository<StoryEntity, Integer>{

}
