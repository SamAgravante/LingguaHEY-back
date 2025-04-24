package edu.cit.lingguahey.Service;

import edu.cit.lingguahey.Entity.ClassroomEntity;
import edu.cit.lingguahey.Entity.UserEntity;
import edu.cit.lingguahey.Repository.ClassroomRepository;
import edu.cit.lingguahey.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepo;
    
    @Autowired
    private UserRepository userRepo;

    private UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByEmail(auth.getName()).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    // Create
    public ClassroomEntity postClassroomEntity(ClassroomEntity classroom) {
        UserEntity teacher = getCurrentUser();
        classroom.setTeacher(teacher);
        return classroomRepo.save(classroom);
    }

    // Read
    public List<ClassroomEntity> getAllClassroomEntity() {
        return classroomRepo.findAll();
    }

    // Read by ID
    public ClassroomEntity getClassroomEntity(int classroomId) throws AccessDeniedException {
        ClassroomEntity classroom = classroomRepo.findById(classroomId)
            .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
        
        UserEntity currentUser = getCurrentUser();
        if (currentUser.getRole().name().equals("TEACHER") && (classroom.getTeacher() == null || !(classroom.getTeacher().getUserId() == currentUser.getUserId()))) {
            throw new AccessDeniedException("You do not own this classroom");
        }

        return classroom;
    }

    // Update
    public ClassroomEntity putClassroomEntity(int classroomId, ClassroomEntity newClassroom) {
        try {
            ClassroomEntity classroom = classroomRepo.findById(classroomId).get();
            classroom.setClassroomName(newClassroom.getClassroomName());
            return classroomRepo.save(classroom);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Classroom " + classroomId + " not found!");
        }
    }

    // Delete
    @SuppressWarnings("unused")
    public String deleteClassroomEntity(int classroomId) {
        if (classroomRepo.findById(classroomId) != null) {
            classroomRepo.deleteById(classroomId);
            return "Choice " + classroomId + " deleted successfully!";
        } else {
            return "Choice " + classroomId + " not found!";
        }
    }
}
