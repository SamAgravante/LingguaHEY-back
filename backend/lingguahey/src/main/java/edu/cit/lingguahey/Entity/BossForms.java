package edu.cit.lingguahey.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "boss_forms")
public class BossForms {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "boss_monster_id", nullable = false)
    private LevelMonster bossMonster;

    @ManyToOne
    @JoinColumn(name = "minion_monster_id", nullable = false)
    private LevelMonster minionMonster;

    public BossForms() {
        super();
    }

    public BossForms(LevelMonster bossMonster, LevelMonster minionMonster) {
        this.bossMonster = bossMonster;
        this.minionMonster = minionMonster;
    }
    
}
