package org.avokado2.rps.maneger;

import lombok.RequiredArgsConstructor;
import org.avokado2.rps.model.GameStatusUpdateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameTopicManager {

    private final SimpMessagingTemplate messagingTemplate;
    @EventListener
    public void handleGameStatusUpdateEvent(GameStatusUpdateEvent event) {
        messagingTemplate.convertAndSendToUser("29", "/game" , event);
        messagingTemplate.convertAndSendToUser(event.getLogin(), "/game" , event);
    }
}
