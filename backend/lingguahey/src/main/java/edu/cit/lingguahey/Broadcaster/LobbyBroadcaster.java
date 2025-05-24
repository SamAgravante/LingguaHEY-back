package edu.cit.lingguahey.Broadcaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import edu.cit.lingguahey.DTO.LobbyUpdate;
import java.util.HashMap;
import java.util.Map;

@Component
public class LobbyBroadcaster {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastLobbyUpdate(int activityId, LobbyUpdate update) {
        messagingTemplate.convertAndSend("/topic/lobby/" + activityId, update);
    }

    public void broadcastStartMessage(int activityId) {
        Map<String, Object> startMsg = new HashMap<>();
        startMsg.put("type", "START");
        messagingTemplate.convertAndSend("/topic/lobby/" + activityId, startMsg);
    }

    public void broadcastStopMessage(int activityId) {
        Map<String, Object> stopMsg = new HashMap<>();
        stopMsg.put("type", "STOP");
        messagingTemplate.convertAndSend("/topic/lobby/" + activityId, stopMsg);
    }
}