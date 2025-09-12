package edu.cit.lingguahey.model;

import java.util.List;

import lombok.Data;

@Data
public class LevelMonsterResponse {
    private int id;
    private String monsterType;
    private MonsterResponse monster;
    private List<MonsterResponse> bossForms;
}
