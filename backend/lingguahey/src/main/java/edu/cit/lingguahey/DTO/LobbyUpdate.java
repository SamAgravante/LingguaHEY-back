/* LobbyUpdate.java */
package edu.cit.lingguahey.DTO;

import java.util.List;
import edu.cit.lingguahey.model.LobbyDTO; // <-- ADD THIS LINE
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LobbyUpdate {
    private String type;
    private List<LobbyDTO> users;

    public LobbyUpdate(List<LobbyDTO> users) {
        this.type = "UPDATE";
        this.users = users;
    }
}
