package edu.cit.lingguahey.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.LiveActivityEntity;

@Repository
public interface LiveActivityRepository extends JpaRepository<LiveActivityEntity, Integer>{
    List<LiveActivityEntity> findByActivityClassroom_ClassroomID(int classroomId);
    Optional<LiveActivityEntity> findByActivityClassroom_ClassroomIDAndIsDeployedTrue(int classroomId);
}
