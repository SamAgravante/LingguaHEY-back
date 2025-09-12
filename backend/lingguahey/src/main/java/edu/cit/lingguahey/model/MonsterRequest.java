package edu.cit.lingguahey.model;

import java.util.List;

import edu.cit.lingguahey.Entity.MonsterType;
import lombok.Data;

@Data
public class MonsterRequest {
    private int monsterId;
    private MonsterType monsterType;
    private List<Integer> bossFormsMinionIds;
}
