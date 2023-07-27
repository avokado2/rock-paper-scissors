package org.avokado2.rps.dao;

import org.avokado2.rps.model.GameEntity;
import org.avokado2.rps.model.GameRoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRoundRepository extends JpaRepository<GameRoundEntity, Long> {
    List<GameRoundEntity> findByGame(GameEntity game);
}
