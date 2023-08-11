package org.avokado2.rps.dao;

import org.avokado2.rps.model.GameEntity;
import org.avokado2.rps.model.GameRequestEntity;
import org.avokado2.rps.model.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepositry extends JpaRepository<GameEntity, Long> {
    List<GameEntity> findByPlayer1AndCompleted(PlayerEntity player1, boolean completed);
    List<GameEntity> findByPlayer2AndCompleted(PlayerEntity player2, boolean completed);
    List<GameEntity> findByPause(boolean pause);
}
