package edu.cit.lingguahey.model;

import java.util.List;

import lombok.Data;

@Data
public class GameSessionResponse {
    private int levelId;
    private int userId;
    private List<MonsterResponse> monsters;
    private int lives;
    private int skipsLeft;
    private int shield;
    private int currentMonsterIndex;
    private int clearedMonstersCount;
    private boolean isGameOver;
}
