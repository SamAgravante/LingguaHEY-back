package edu.cit.lingguahey.model;

import java.util.List;

import lombok.Data;

@Data
public class LevelEditRequest {
    private String levelName;
    private List<MonsterRequest> monsters;
    private int coinsReward;
    private int gemsReward;
}
