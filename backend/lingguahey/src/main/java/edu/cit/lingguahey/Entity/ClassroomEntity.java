package edu.cit.lingguahey.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class ClassroomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private int classroomID;

    private String classroomName;

    // Connections
    @OneToMany(mappedBy = "classroom")
    //@JsonManagedReference(value = "classroom-users")
    @JsonIgnore
    private List<UserEntity> users;

    @OneToMany(mappedBy = "activityClassroom")
    //@JsonManagedReference(value = "classroom-liveactivities")
    @JsonIgnore
    private List<LiveActivityEntity> activities;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonBackReference(value = "classroom-teacher")
    private UserEntity teacher;

    // Constructors and Getter Setter
    public ClassroomEntity() {
    }

    public ClassroomEntity(String classroomName) {
        this.classroomName = classroomName;
    }

    public int getClassroomID() {
        return classroomID;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    public List<LiveActivityEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<LiveActivityEntity> activities) {
        this.activities = activities;
    }

    public UserEntity getTeacher() {
        return teacher;
    }

    public void setTeacher(UserEntity teacher) {
        this.teacher = teacher;
    }
    
    
}