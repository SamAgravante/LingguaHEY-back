package edu.cit.lingguahey.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.lingguahey.Entity.ClassroomUser;

@Repository
public interface ClassroomUserRepository extends JpaRepository<ClassroomUser, Integer> {
    Optional<ClassroomUser> findByClassroom_ClassroomIDAndUser_UserId(int classroomId, int userId);
    List<ClassroomUser> findByClassroom_ClassroomID(int classroomId);
    Optional<ClassroomUser> findByUser_UserId(int userId);
    void deleteByClassroom_ClassroomIDAndUser_UserId(int classroomId, int userId);
}