package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.QuestionEntity;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {
    
}
