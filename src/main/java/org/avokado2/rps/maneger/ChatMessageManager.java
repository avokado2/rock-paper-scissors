package org.avokado2.rps.maneger;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.avokado2.rps.dao.ChatMessageRepositry;
import org.avokado2.rps.dao.PlayerRepositry;
import org.avokado2.rps.exception.ManagerException;
import org.avokado2.rps.model.ChatMessageEntity;
import org.avokado2.rps.model.PlayerEntity;
import org.avokado2.rps.protocol.ChatMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageManager {

    private final ChatMessageRepositry chatMessageRepositry;

    private final PlayerManager playerManager;

    private final PlayerRepositry playerRepositry;

    private final ApplicationEventPublisher publisher;

    private final SettingManager settingManager;

    private final ChatIntervalManager chatIntervalManager;

    @Transactional
    public void addMessage(long gameId, String message){
        long minMessageIntervalMs = settingManager.getMinMessageIntervalMs();
        Long ts = chatIntervalManager.getLastMessageTimestamp(playerManager.getCurrentPlayerId());
        if (ts != null && System.currentTimeMillis() - ts < minMessageIntervalMs) {
            throw new ManagerException("sending too often , try again later");
        }
        ChatMessageEntity msg = new ChatMessageEntity();
        msg.setGameId(gameId);
        msg.setMessage(message);
        PlayerEntity playerE = playerRepositry.getReferenceById(playerManager.getCurrentPlayerId());
        msg.setPlayer(playerE);
        chatMessageRepositry.save(msg);

        ChatMessage chatMessageEvent = new ChatMessage();
        chatMessageEvent.setText(msg.getMessage());
        chatMessageEvent.setNickname(playerE.getLogin());
        chatMessageEvent.setGameId(msg.getGameId());
        publisher.publishEvent(chatMessageEvent);
        chatIntervalManager.messageAdded(playerManager.getCurrentPlayerId());
    }
    public List<ChatMessage> getMessages(long gameId){
        Page<ChatMessageEntity> chatMessageEntityPage = chatMessageRepositry.findByGameId(gameId,
                PageRequest.of(0, 10, Sort.by(Sort.Order.desc("timestamp"))));
        List<ChatMessage> messageEvents = new ArrayList<>();
        for (ChatMessageEntity chatMessage: chatMessageEntityPage.getContent()) {
            ChatMessage chatMessageEvent = new ChatMessage();
            chatMessageEvent.setGameId(chatMessage.getGameId());
            chatMessageEvent.setText(chatMessage.getMessage());
            chatMessageEvent.setNickname(chatMessage.getPlayer().getLogin());
            messageEvents.add(chatMessageEvent);
        }
        return messageEvents;
    }
}
