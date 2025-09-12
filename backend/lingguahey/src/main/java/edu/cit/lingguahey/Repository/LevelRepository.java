package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.LevelEntity;

@Repository
public interface LevelRepository extends JpaRepository<LevelEntity, Integer> {
    
}
