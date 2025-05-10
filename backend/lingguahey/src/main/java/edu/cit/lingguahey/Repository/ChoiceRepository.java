package edu.cit.lingguahey.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.ChoiceEntity;

@Repository
public interface ChoiceRepository extends JpaRepository<ChoiceEntity, Integer>{
    List<ChoiceEntity> findByQuestion_QuestionId(int questionId);
}
