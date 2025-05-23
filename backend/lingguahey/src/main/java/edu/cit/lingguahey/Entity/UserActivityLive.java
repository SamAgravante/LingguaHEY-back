package edu.cit.lingguahey.Entity;

import jakarta.persistence.Column;
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
@Table(name = "user_live_activities")
public class UserActivityLive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private LiveActivityEntity activity;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean inLobby = true; // true if user is in lobby, false if moved to activity

    @Column(nullable = false)
    private int score = 0; // <-- Add this field

    public UserActivityLive() {
        super();
    }

    public UserActivityLive(UserEntity user, LiveActivityEntity activity) {
        this.user = user;
        this.activity = activity;
        this.inLobby = true;
    }

    public int getScore() { // <-- Add this getter
        return score;
    }

    public void setScore(int score) { // <-- Add this setter
        this.score = score;
    }
}
