package edu.cit.lingguahey.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.ClassroomActivityLive;
import edu.cit.lingguahey.model.ClassroomActivityLiveProjection;

@Repository
public interface ClassroomActivityLiveRepository extends JpaRepository<ClassroomActivityLive, Integer> {
    Optional<ClassroomActivityLive> findByClassroom_ClassroomIDAndActivity_ActivityId(int classroomId, int activityId);
    List<ClassroomActivityLiveProjection> findByClassroom_ClassroomID(int classroomId);
    List<ClassroomActivityLive> findByActivity_ActivityId(int activityId);
    Optional<ClassroomActivityLive> findByClassroom_ClassroomIDAndDeployedTrue(int classroomId);
}
