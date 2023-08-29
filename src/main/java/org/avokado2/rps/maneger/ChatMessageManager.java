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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class ChatMessageManager {

    private final ChatMessageRepositry chatMessageRepositry;

    private final PlayerManager playerManager;

    private final PlayerRepositry playerRepositry;

    private final ApplicationEventPublisher publisher;

    private final SettingManager settingManager;

    private final ChatIntervalManager chatIntervalManager;

    private final Pattern longWorld = Pattern.compile("[^ ]{20,}");

    @Transactional
    public void addMessage(long gameId, String message, String toNickname){
        long minMessageIntervalMs = settingManager.getMinMessageIntervalMs();
        Long ts = chatIntervalManager.getLastMessageTimestamp(playerManager.getCurrentPlayerId());
        if (ts != null && System.currentTimeMillis() - ts < minMessageIntervalMs) {
            long ms = minMessageIntervalMs - (System.currentTimeMillis() - ts);
            long s = Math.round(ms / 1000.0);
            throw new ManagerException("You can send a message in " + s + " seconds");
        }
        if (message.length() > 120) {
            throw  new ManagerException("Overlong message");
        }
        Matcher m = longWorld.matcher(message);
        if (m.find()) {
            throw new ManagerException("A message cannot be longer than 20 characters");
        }
        ChatMessageEntity msg = new ChatMessageEntity();
        if (toNickname != null) {
            List<PlayerEntity> toNicknameL = playerRepositry.findByLogin(toNickname);
            if (toNicknameL.isEmpty()) {
                throw new ManagerException("No player with that nickname was found");
            }
            msg.setRecipient(toNicknameL.get(0));
        }
        msg.setGameId(gameId);
        msg.setMessage(message);
        PlayerEntity playerE = playerRepositry.getReferenceById(playerManager.getCurrentPlayerId());
        msg.setPlayer(playerE);
        chatMessageRepositry.save(msg);

        ChatMessage chatMessageEvent = new ChatMessage();
        chatMessageEvent.setText(msg.getMessage());
        chatMessageEvent.setNickname(playerE.getLogin());
        chatMessageEvent.setGameId(msg.getGameId());
        chatMessageEvent.setId(msg.getId());
        chatMessageEvent.setPrivateMessage(msg.getRecipient() != null);
        if (msg.getRecipient() != null) {
            chatMessageEvent.setRecipient(msg.getRecipient().getLogin());
        }
        publisher.publishEvent(chatMessageEvent);
        chatIntervalManager.messageAdded(playerManager.getCurrentPlayerId());
    }
    public List<ChatMessage> getMessages(long gameId){
//        Page<ChatMessageEntity> chatMessageEntityPage = chatMessageRepositry.findByGameId(gameId,
//                PageRequest.of(0, 10, Sort.by(Sort.Order.desc("timestamp"))));
        List<ChatMessageEntity> chatMessageEntities = chatMessageRepositry.getPlayerMessages(playerManager.getCurrentPlayerId(),
                gameId, 10);
        List<ChatMessage> messageEvents = new ArrayList<>();
        for (ChatMessageEntity chatMessage: chatMessageEntities) {
            ChatMessage chatMessageEvent = new ChatMessage();
            chatMessageEvent.setGameId(chatMessage.getGameId());
            chatMessageEvent.setText(chatMessage.getMessage());
            chatMessageEvent.setNickname(chatMessage.getPlayer().getLogin());
            chatMessageEvent.setId(chatMessage.getId());
            chatMessageEvent.setPrivateMessage(chatMessage.getRecipient() != null);
            messageEvents.add(chatMessageEvent);
        }
        return messageEvents;
    }
}
