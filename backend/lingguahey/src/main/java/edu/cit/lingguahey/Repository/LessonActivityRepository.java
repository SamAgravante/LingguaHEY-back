package edu.cit.lingguahey.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.LessonActivityEntity;

@Repository
public interface LessonActivityRepository extends JpaRepository<LessonActivityEntity, Integer>{
    List<LessonActivityEntity> findByLessonClassroom_ClassroomID(int classroomId);
}
