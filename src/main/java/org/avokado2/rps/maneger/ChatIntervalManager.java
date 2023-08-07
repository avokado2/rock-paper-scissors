package org.avokado2.rps.maneger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatIntervalManager {

    @CachePut(value = "player-message-ts", key = "'player_'.concat(#playerId)")
    public long messageAdded(int playerId) {

        return System.currentTimeMillis();
    }
    @Cacheable(value = "player-message-ts", key = "'player_'.concat(#playerId)")
    public Long getLastMessageTimestamp(int playerId) {
        return null;
    }
}
