package edu.cit.lingguahey.Broadcaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import edu.cit.lingguahey.DTO.LiveActivityUpdate;

@Component
public class LiveActivityBroadcaster {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastUpdate(int activityId, LiveActivityUpdate update) {
        messagingTemplate.convertAndSend("/topic/activity/" + activityId, update);
    }
}