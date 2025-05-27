package edu.cit.lingguahey.model;

public interface LeaderboardEntry {
    Integer getUserId();
    String getFirstName();
    String getLastName();
    Integer getTotalScore();
    Integer getProfilePic(); // Assuming profilePic is an Integer, adjust as necessary
}
