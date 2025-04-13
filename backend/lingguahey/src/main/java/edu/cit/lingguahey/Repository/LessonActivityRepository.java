package edu.cit.lingguahey.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.LessonActivityEntity;

@Repository
public interface LessonActivityRepository extends JpaRepository<LessonActivityEntity, Integer>{

}
