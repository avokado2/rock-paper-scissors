package org.avokado2.rps.dao;

import org.avokado2.rps.model.ChatMessageEntity;
import org.avokado2.rps.model.GameEntity;
import org.avokado2.rps.model.PlayerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatMessageRepositry extends CrudRepository<ChatMessageEntity, Long> {

    Page<ChatMessageEntity> findByGameId(long gameId, Pageable pageable);

    List<ChatMessageEntity> findByPlayerId(long playerId);

    @Query(value = "SELECT *\n" +
            "FROM chat_messages\n" +
            "WHERE (recipient_id IS NULL\n" +
            "  OR recipient_id = :playerId \n" +
            "  OR player_id = :playerId)\n" +
            "  AND game_id = :gameId\n" +
            "ORDER BY timestamp DESC\n" +
            "LIMIT :count", nativeQuery = true)
    List<ChatMessageEntity> getPlayerMessages(int playerId, long gameId, int count);

    @Modifying
    @Query(value = "DELETE FROM chat_messages\n" +
            "WHERE player_id = :playerId", nativeQuery = true)
    void deleteMessage(int playerId);
}
