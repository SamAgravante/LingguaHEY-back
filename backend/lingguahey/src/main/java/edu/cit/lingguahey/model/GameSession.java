package edu.cit.lingguahey.model;

import java.util.List;

import edu.cit.lingguahey.Entity.LevelMonster;
import lombok.Data;

@Data
public class GameSession {
    private int userId;
    private int levelId;
    private int lives;
    private int skipsLeft;
    private int shield;
    private int clearedMonstersCount;
    private int currentMonsterIndex;
    private List<LevelMonster> monsters;
    private boolean isGameOver;
    private boolean isBossEncounter;
    private List<Integer> bossFormsMinionIds;
    private int currentBossFormIndex;
}
