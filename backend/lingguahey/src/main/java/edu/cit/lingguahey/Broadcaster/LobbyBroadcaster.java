package edu.cit.lingguahey.Broadcaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import edu.cit.lingguahey.DTO.LobbyUpdate;

@Component
public class LobbyBroadcaster {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastLobbyUpdate(int activityId, LobbyUpdate update) {
        messagingTemplate.convertAndSend("/topic/lobby/" + activityId, update);
    }
}