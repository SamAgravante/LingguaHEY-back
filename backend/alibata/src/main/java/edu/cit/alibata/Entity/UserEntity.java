package edu.cit.alibata.Entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import edu.cit.alibata.token.Token;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    @Builder.Default
    private boolean subscriptionStatus = false;

    //Spring Security ug JWT
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
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
    @OneToMany(mappedBy = "user")
    private List<ScoreEntity> scores;

    @ManyToMany
    @JoinTable(
        name = "user_stories",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "story_id")
    )
    private List<StoryEntity> stories;

    @ManyToMany
    @JoinTable(
        name = "user_activities",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private List<ActivityEntity> activities;

    public UserEntity(){
        super();
    }

    public UserEntity(String firstName, String middleName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public UserEntity(String firstName, String middleName, String lastName, String email, String password, boolean subscriptionStatus, Role role) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.subscriptionStatus = subscriptionStatus;
        this.role = role;
    }

    public UserEntity(int userId, String firstName, String middleName, String lastName, String email, String password,
            boolean subscriptionStatus, Role role, List<Token> tokens, List<ScoreEntity> scores,
            List<StoryEntity> stories, List<ActivityEntity> activities) {
        this.userId = userId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.subscriptionStatus = subscriptionStatus;
        this.role = role;
        this.tokens = tokens;
        this.scores = scores;
        this.stories = stories;
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

    public boolean getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(boolean subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public List<ScoreEntity> getScores() {
        return scores;
    }
    
    public void setScores(List<ScoreEntity> scores) {
        this.scores = scores;
    }
    
    public List<StoryEntity> getStories() {
        return stories;
    }
    
    public void setStories(List<StoryEntity> stories) {
        this.stories = stories;
    }
    
    public List<ActivityEntity> getActivities() {
        return activities;
    }
    
    public void setActivities(List<ActivityEntity> activities) {
        this.activities = activities;
    }
    
}
