package edu.cit.lingguahey.DTO;

import java.util.List;
import edu.cit.lingguahey.model.LobbyDTO;

public class LobbyUpdate {
    private List<LobbyDTO> users;

    public LobbyUpdate(List<LobbyDTO> users) {
        this.users = users;
    }

    public List<LobbyDTO> getUsers() {
        return users;
    }

    public void setUsers(List<LobbyDTO> users) {
        this.users = users;
    }
}