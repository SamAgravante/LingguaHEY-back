package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.ScoreEntity;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
    
}
