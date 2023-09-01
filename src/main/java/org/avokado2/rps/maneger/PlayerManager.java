package org.avokado2.rps.maneger;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.avokado2.rps.dao.ChatMessageRepositry;
import org.avokado2.rps.dao.PlayerRepositry;
import org.avokado2.rps.exception.ManagerException;
import org.avokado2.rps.model.PlayerEntity;
import org.avokado2.rps.model.PlayerUser;
import org.avokado2.rps.protocol.BlockPlayerEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class PlayerManager {

    private final PlayerRepositry playerRepositry;

    private final PasswordEncoder passwordEncoder;

    private final ChatMessageRepositry chatMessageRepositry;

    private final ApplicationEventPublisher publisher;

    @Transactional
    public boolean registerPlayer(String login, String password) {
        List<PlayerEntity> players = playerRepositry.findByLogin(login);

        if (players.size() > 0){
            return false;
        }
        PlayerEntity player = new PlayerEntity();
        player.setLogin(login);
        player.setPasswordHash(passwordEncoder.encode(password));
        player.setRating(100);
        playerRepositry.save(player);
        return true;
    }

    public int getCurrentPlayerId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("unlogged");
        }
        Object details = authentication.getPrincipal();
        if (!(details instanceof PlayerUser)) {
            throw  new RuntimeException("unlogged");
        }
        PlayerUser playerUser = (PlayerUser) details;
        return playerUser.getId();
    }

    public PlayerEntity getCurrentPlayer() {
        PlayerEntity currentPlayer = playerRepositry.findById(getCurrentPlayerId());
        return currentPlayer;
    }

    @Transactional
    public void blockPlayer(String nickname) {
        if (!getCurrentPlayer().isAdmin()) {
            throw new ManagerException("You're not an admin and you're about to be banned");
        }
        if (getCurrentPlayer().isBlocked()) {
            throw new ManagerException("You're already locked out");
        }
        List<PlayerEntity> block = playerRepositry.findByLogin(nickname);
        if (block.isEmpty()) {
            throw new ManagerException("player not found");
        }
        PlayerEntity blockPlayer = block.get(0);
        blockPlayer.setBlocked(true);
        chatMessageRepositry.deleteMessage(blockPlayer.getId());
        BlockPlayerEvent blockPlayerEvent = new BlockPlayerEvent();
        blockPlayerEvent.setNickname(blockPlayer.getLogin());

        publisher.publishEvent(blockPlayerEvent);
    }
}
