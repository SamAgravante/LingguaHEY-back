package edu.cit.lingguahey.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.lingguahey.Entity.ClassroomActivityLive;
import edu.cit.lingguahey.model.ClassroomActivityLiveProjection;

public interface ClassroomActivityLiveRepository extends JpaRepository<ClassroomActivityLive, Integer> {
    Optional<ClassroomActivityLive> findByClassroom_ClassroomIDAndActivity_ActivityId(int classroomId, int activityId);
    List<ClassroomActivityLiveProjection> findByClassroom_ClassroomID(int classroomId);
}
