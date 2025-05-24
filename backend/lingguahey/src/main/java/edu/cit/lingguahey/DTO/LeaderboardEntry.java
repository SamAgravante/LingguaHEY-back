package edu.cit.lingguahey.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private int userId;
    private String name;
    private int score;
}
