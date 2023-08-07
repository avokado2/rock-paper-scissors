package org.avokado2.rps.maneger;

import lombok.RequiredArgsConstructor;
import org.avokado2.rps.protocol.ChatMessage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatTopicManager {

    private final SimpMessagingTemplate messagingTemplate;
    @EventListener
    public void handleNewChatMessageEvent(ChatMessage event) {
        messagingTemplate.convertAndSend("/topic/chat-messages/" + event.getGameId(), event);
    }
}
