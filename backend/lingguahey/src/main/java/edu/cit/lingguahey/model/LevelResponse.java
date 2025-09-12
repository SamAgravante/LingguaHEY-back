package edu.cit.lingguahey.model;

import java.util.List;

import lombok.Data;

@Data
public class LevelResponse {
    private int levelId;
    private String levelName;
    private int coinsReward;
    private int gemsReward;
    private List<LevelMonsterResponse> levelMonsters;
}
