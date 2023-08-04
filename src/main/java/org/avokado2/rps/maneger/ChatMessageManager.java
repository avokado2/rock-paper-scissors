package org.avokado2.rps.maneger;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.avokado2.rps.dao.ChatMessageRepositry;
import org.avokado2.rps.dao.PlayerRepositry;
import org.avokado2.rps.event.NewChatMessageEvent;
import org.avokado2.rps.exception.ManagerException;
import org.avokado2.rps.model.ChatMessageEntity;
import org.avokado2.rps.model.PlayerEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageManager {

    private final ChatMessageRepositry chatMessageRepositry;

    private final PlayerManager playerManager;

    private final PlayerRepositry playerRepositry;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public void addMessage(long gameId, String message){
        if ((gameId % 2) == 0) {
            throw new ManagerException("error");
        }
        ChatMessageEntity msg = new ChatMessageEntity();
        msg.setGameId(gameId);
        msg.setMessage(message);
        PlayerEntity playerE = playerRepositry.getReferenceById(playerManager.getCurrentPlayerId());
        msg.setPlayer(playerE);
        chatMessageRepositry.save(msg);

        NewChatMessageEvent chatMessageEvent = new NewChatMessageEvent();
        chatMessageEvent.setText(msg.getMessage());
        chatMessageEvent.setNickname(playerE.getLogin());
        chatMessageEvent.setGameId(msg.getGameId());
        publisher.publishEvent(chatMessageEvent);
    }
    public List<ChatMessageEntity> getMessages(long gameId){
        Page<ChatMessageEntity> chatMessageEntityPage = chatMessageRepositry.findByGameId(gameId,
                PageRequest.of(0, 10, Sort.by(Sort.Order.desc("timestamp"))));
        return  chatMessageEntityPage.getContent();
    }
}