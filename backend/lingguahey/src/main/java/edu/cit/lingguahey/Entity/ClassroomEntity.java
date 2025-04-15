package edu.cit.lingguahey.Entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class ClassroomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Long classroomID;

    private String classroomName;

    // Connections
    @OneToMany(mappedBy = "classroom")
    private List<UserEntity> users;

    @OneToMany(mappedBy = "lessonClassroom")
    private List<LessonActivityEntity> lessons;

    @OneToMany(mappedBy = "activityClassroom")
    private List<LiveActivityEntity> activities;

    // Constructors and Getter Setter
    public ClassroomEntity() {
    }

    public ClassroomEntity(String classroomName) {
        this.classroomName = classroomName;
    }

    public Long getClassroomID() {
        return classroomID;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }
}