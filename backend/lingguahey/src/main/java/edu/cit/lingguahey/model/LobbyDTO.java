package edu.cit.lingguahey.model;

public class LobbyDTO {
    private int userId;
    private String firstName;
    private String lastName;
    private String role;
    private int profilePic; // Add this field

    public LobbyDTO(int userId, String firstName, String lastName, String role, int profilePic) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profilePic = profilePic;
    }

    // Getters and setters (optional, but recommended)
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public int getProfilePic() { return profilePic; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setRole(String role) { this.role = role; }
    public void setProfilePic(int profilePic) { this.profilePic = profilePic; }
}