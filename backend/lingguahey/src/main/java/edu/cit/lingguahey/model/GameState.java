package edu.cit.lingguahey.model;

import java.util.List;

import edu.cit.lingguahey.Entity.MonsterEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameState {
    private String userId;
    private int levelId;
    private int currentRound;
    private List<MonsterEntity> remainingMonsters;
    private int currentMonsterHealth;
    private String currentMonsterName;
    private String currentMonsterJumbledLetters;
    private boolean isBossFight;
    private int bossLives;
}
