package org.avokado2.rps.maneger;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.avokado2.rps.dao.PlayerRepositry;
import org.avokado2.rps.model.PlayerEntity;
import org.avokado2.rps.model.PlayerUser;
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
}
