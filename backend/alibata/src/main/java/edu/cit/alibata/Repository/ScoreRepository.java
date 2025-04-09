package edu.cit.alibata.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.alibata.Entity.ScoreEntity;

public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
    
}
