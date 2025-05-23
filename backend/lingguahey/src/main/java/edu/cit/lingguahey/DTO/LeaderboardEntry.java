package edu.cit.lingguahey.DTO;

public class LeaderboardEntry {
    private int userId;
    private String name;
    private int score;

    public LeaderboardEntry(int userId, String name, int score) {
        this.userId = userId;
        this.name = name;
        this.score = score;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public int getScore() { return score; }
}
