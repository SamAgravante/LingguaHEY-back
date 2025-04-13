package edu.cit.lingguahey.Service;

import edu.cit.lingguahey.Entity.ClassroomEntity;
import edu.cit.lingguahey.Repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepo;


    // Create
    public ClassroomEntity createClassroom(ClassroomEntity classroom) {
        return classroomRepo.save(classroom);
    }

    // Read
    public List<ClassroomEntity> getAllClassrooms() {
        return classroomRepo.findAll();
    }

    // Read by ID
    public Optional<ClassroomEntity> getClassroomById(int id) {
        return classroomRepo.findById(id);
    }

    // Update
    public ClassroomEntity updateClassroom(int id, ClassroomEntity updatedClassroom) {
        return classroomRepo.findById(id).map(classroom -> {
            classroom.setClassroomName(updatedClassroom.getClassroomName());
            return classroomRepo.save(classroom);
        }).orElse(null);
    }

    // Delete
    public boolean deleteClassroom(int id) {
        return classroomRepo.findById(id).map(classroom -> {
            classroomRepo.delete(classroom);
            return true;
        }).orElse(false);
    }
}
