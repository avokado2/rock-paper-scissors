package org.avokado2.rps.maneger;

import lombok.RequiredArgsConstructor;
import org.avokado2.rps.protocol.BlockPlayerEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Service
public class PlayerTopicManager {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBlockPlayerEvent(BlockPlayerEvent event) {
        messagingTemplate.convertAndSend("/topic/block-player-event", event);
    }

}
