package edu.cit.lingguahey.DTO;

import java.util.List;
import edu.cit.lingguahey.Entity.UserEntity;

public class LobbyUpdate {
    private List<UserEntity> users;

    public LobbyUpdate(List<UserEntity> users) {
        this.users = users;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }
}