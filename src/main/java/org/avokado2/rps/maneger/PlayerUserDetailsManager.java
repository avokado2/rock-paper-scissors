package org.avokado2.rps.maneger;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.dao.PlayerRepositry;
import org.avokado2.rps.model.PlayerEntity;
import org.avokado2.rps.model.PlayerUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerUserDetailsManager implements UserDetailsManager {
    @Autowired
    private  PlayerRepositry playerRepositry;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static final String PLAYER_AUTH = "PLAYER";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<PlayerEntity> players = playerRepositry.findByLogin(username);
        if (players.size() == 0) {
            return null;
        }
        if (players.size() > 1) {
            throw  new RuntimeException("more than one player with this nickname was found");
        }
        PlayerEntity player = players.get(0);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(PLAYER_AUTH);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        PlayerUser playerUser = new PlayerUser(player.getLogin(), player.getPasswordHash(), !player.isBlocked(),
                true, true, !player.isBlocked(), authorities, player.getId());
        return playerUser;
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        List<PlayerEntity> players = playerRepositry.findByLogin(username);

        return players.size() > 0;
    }
}
