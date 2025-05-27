package edu.cit.lingguahey.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private int userId;
    private String name;
    private int score;
    private int profilePic; // Assuming profilePic is an Integer, adjust as necessary
}
