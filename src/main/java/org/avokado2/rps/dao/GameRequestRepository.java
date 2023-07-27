package org.avokado2.rps.dao;

import org.avokado2.rps.model.GameEntity;
import org.avokado2.rps.model.GameRequestEntity;
import org.avokado2.rps.model.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRequestRepository extends JpaRepository<GameRequestEntity, Long> {
    List<GameRequestEntity> findByPlayer(PlayerEntity player);
}
