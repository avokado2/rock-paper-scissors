package org.avokado2.rps.maneger;

import lombok.RequiredArgsConstructor;
import org.avokado2.rps.model.GameStatusUpdateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class GameTopicManager {

    private final SimpMessagingTemplate messagingTemplate;
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGameStatusUpdateEvent(GameStatusUpdateEvent event) {
        messagingTemplate.convertAndSendToUser(event.getLogin(), "/queue/game" , event);
    }
}
