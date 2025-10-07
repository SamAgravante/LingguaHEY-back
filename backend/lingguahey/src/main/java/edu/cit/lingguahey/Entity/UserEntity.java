package edu.cit.lingguahey.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import edu.cit.lingguahey.token.Token;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Builder.Default
    private int gems = 0;
    @Builder.Default
    private int coins = 0;
    @Builder.Default
    private int lives = 4;
    @Builder.Default
    private int shield = 0;
    @Builder.Default
    private int skipsLeft = 0;

    @ElementCollection
    @CollectionTable(name = "user_potions", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "potion_name")
    @Column(name = "quantity")
    private Map<String, Integer> potions;
    
    @Builder.Default
    private boolean subscriptionStatus = false;
    @Builder.Default
    private SubscriptionType subscriptionType = SubscriptionType.FREE;

    @Column(name = "subscription_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date subscriptionStartDate;

    @Column(name = "subscription_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date subscriptionEndDate;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "verification_token")
    private String verificationToken;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    public enum SubscriptionType {
        FREE,
        PREMIUM,
        PREMIUM_PLUS
    }

    @Column(nullable = true)
    private int profilePic;

    //Spring Security ug JWT
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
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
        return this.enabled;
    }

    //Entity Relations

    @ManyToMany
    @JoinTable(
        name = "user_cosmetics",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "cosmetic_id")
    )
    @JsonIgnore
    private List<CosmeticEntity> inventory;

    @ManyToOne
    @JoinColumn(name = "equipped_cosmetic_id")
    @JsonIgnore
    private CosmeticEntity equippedCosmetic;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    //@JsonBackReference(value = "classroom-users")
    @JsonIgnore
    private ClassroomEntity classroom;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-scores")
    private List<ScoreEntity> scores;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "classroom-teacher")
    private List<ClassroomEntity> classrooms;

    @ManyToMany
    @JoinTable(
        name = "user_activities",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )

    @ManyToOne
    @JoinColumn(name = "activity_id")
    @JsonBackReference(value = "live-users")
    private LiveActivityEntity liveActivity;
    
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
    
    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public int getSkipsLeft() {
        return skipsLeft;
    }

    public void setSkipsLeft(int skipsLeft) {
        this.skipsLeft = skipsLeft;
    }

    public Map<String, Integer> getPotions() {
        return potions;
    }

    public void setPotions(Map<String, Integer> potions) {
        this.potions = potions;
    }

    public List<CosmeticEntity> getInventory() {
        return inventory;
    }

    public void setInventory(List<CosmeticEntity> inventory) {
        this.inventory = inventory;
    }

    public CosmeticEntity getEquippedCosmetic() {
        return equippedCosmetic;
    }

    public void setEquippedCosmetic(CosmeticEntity equippedCosmetic) {
        this.equippedCosmetic = equippedCosmetic;
    }

    public boolean getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(boolean subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
    public Date getSubscriptionStartDate() {
            return subscriptionStartDate;
    }
    public void setSubscriptionStartDate(Date subscriptionStartDate) {
            this.subscriptionStartDate = subscriptionStartDate;
    }
    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}
