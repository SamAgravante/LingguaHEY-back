package edu.cit.lingguahey.Entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "level_entity")
public class LevelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private int levelId;

    @Column(unique = true, nullable = false)
    private String levelName;

    private int coinsReward;
    private int gemsReward;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LevelMonster> levelMonsters;

    public LevelEntity(){
        super();
    }

    public LevelEntity(String levelName, List<LevelMonster> levelMonsters, int coinsReward, int gemsReward) {
        this.levelName = levelName;
        this.levelMonsters = levelMonsters;
        this.coinsReward = coinsReward;
        this.gemsReward =gemsReward;
    }
    
}
