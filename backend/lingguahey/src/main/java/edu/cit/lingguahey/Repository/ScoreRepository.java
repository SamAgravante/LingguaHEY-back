package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.lingguahey.Entity.ScoreEntity;

public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
    
}
