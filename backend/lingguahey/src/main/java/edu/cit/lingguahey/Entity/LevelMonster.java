package edu.cit.lingguahey.Entity;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "level_monsters")
public class LevelMonster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private LevelEntity level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MonsterEntity monster;

    @Enumerated(EnumType.STRING)
    @Column(name = "monster_type", nullable = false)
    private MonsterType monsterType;

    @OneToMany(mappedBy = "bossMonster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BossForms> minionForms;

    public LevelMonster(){
        super();
    }

    public LevelMonster(LevelEntity level, MonsterEntity monster, MonsterType monsterType) {
        this.level = level;
        this.monster = monster;
        this.monsterType = monsterType;
    }
    
}
