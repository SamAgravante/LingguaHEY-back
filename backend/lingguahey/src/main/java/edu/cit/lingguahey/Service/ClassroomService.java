package edu.cit.lingguahey.Service;

import edu.cit.lingguahey.Entity.ClassroomEntity;
import edu.cit.lingguahey.Repository.ClassroomRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepo;


    // Create
    public ClassroomEntity postClassroomEntity(ClassroomEntity classroom) {
        return classroomRepo.save(classroom);
    }

    // Read
    public List<ClassroomEntity> getAllClassroomEntity() {
        return classroomRepo.findAll();
    }

    // Read by ID
    public ClassroomEntity getClassroomEntity(int classroomId) {
        return classroomRepo.findById(classroomId).get();
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
