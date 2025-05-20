/* LobbyUpdate.java */
package edu.cit.lingguahey.DTO;

import java.util.List;
import edu.cit.lingguahey.model.LobbyDTO; // <-- ADD THIS LINE

public class LobbyUpdate {
    private String type;
    private List<LobbyDTO> users;

    public LobbyUpdate() {
    }

    /**
     * Constructs an UPDATE message containing the current lobby users.
     */
    public LobbyUpdate(List<LobbyDTO> users) {
        this.type = "UPDATE";
        this.users = users;
    }

    /**
     * Constructs a message with only a type (e.g. "START").
     */
    public LobbyUpdate(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LobbyDTO> getUsers() {
        return users;
    }

    public void setUsers(List<LobbyDTO> users) {
        this.users = users;
    }
}
