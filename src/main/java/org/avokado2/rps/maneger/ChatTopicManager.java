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
        if (event.isPrivateMessage()){
            messagingTemplate.convertAndSendToUser(event.getNickname(), "/queue/chat-messages" , event);
            messagingTemplate.convertAndSendToUser(event.getRecipient(), "/queue/chat-messages" , event);
        } else {
            messagingTemplate.convertAndSend("/topic/chat-messages/" + event.getGameId(), event);
        }
    }
}
