package edu.cit.lingguahey.Entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import edu.cit.lingguahey.token.Token;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;

@Builder
@Entity
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private String idNumber;
    private int totalPoints;
    @Builder.Default
    private boolean subscriptionStatus = false;
    @Lob
    @Column(name = "profile_picture", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] profilePic;

    //Spring Security ug JWT
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference(value = "user-tokens")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //Entity Relations
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    @JsonBackReference(value = "classroom-users")
    private ClassroomEntity classroom;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-scores")
    private List<ScoreEntity> scores;

    @OneToMany(mappedBy = "teacher")
    @JsonManagedReference(value = "classroom-teacher")
    private List<ClassroomEntity> classrooms;

    @ManyToMany
    @JoinTable(
        name = "user_activities",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    //@JsonManagedReference
    @JsonIgnore
    private List<LessonActivityEntity> activities;

    public UserEntity(){
        super();
    }

    public UserEntity(String firstName, String middleName, String lastName, String email, String password, String idNumber, int totalPoints, Role role) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.idNumber = idNumber;
        this.totalPoints = totalPoints;
        this.role = role;
    }

    public UserEntity(String firstName, String middleName, String lastName, String email, String password, String idNumber, int totalPoints, boolean subscriptionStatus, byte[] profilePic, Role role) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.idNumber = idNumber;
        this.totalPoints = totalPoints;
        this.subscriptionStatus = subscriptionStatus;
        this.profilePic = profilePic;
        this.role = role;
    }
    
    public UserEntity(int userId, String firstName, String middleName, String lastName, String email, String password,
            String idNumber, int totalPoints, boolean subscriptionStatus, byte[] profilePic, Role role, List<Token> tokens,
            ClassroomEntity classroom, List<ScoreEntity> scores, List<ClassroomEntity> classrooms, List<LessonActivityEntity> activities) {
        this.userId = userId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.idNumber = idNumber;
        this.totalPoints = totalPoints;
        this.subscriptionStatus = subscriptionStatus;
        this.profilePic = profilePic;
        this.role = role;
        this.tokens = tokens;
        this.classroom = classroom;
        this.scores = scores;
        this.classrooms = classrooms;
        this.activities = activities;
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public boolean getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(boolean subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<ScoreEntity> getScores() {
        return scores;
    }
    
    public void setScores(List<ScoreEntity> scores) {
        this.scores = scores;
    }

    public ClassroomEntity getClassroom() {
        return classroom;
    }

    public void setClassroom(ClassroomEntity classroom) {
        this.classroom = classroom;
    }

    public List<ClassroomEntity> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<ClassroomEntity> classrooms) {
        this.classrooms = classrooms;
    }
    
}
