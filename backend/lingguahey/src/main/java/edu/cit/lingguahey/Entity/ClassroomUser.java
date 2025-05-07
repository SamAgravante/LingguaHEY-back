package edu.cit.lingguahey.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "classroom_users")
public class ClassroomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassroomEntity classroom;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String classroomName;

    public ClassroomUser() {
        super();
    }
    
    public ClassroomUser(int id, ClassroomEntity classroom, UserEntity user, String classroomName) {
        this.id = id;
        this.classroom = classroom;
        this.user = user;
        this.classroomName = classroomName;
    }
    
}
