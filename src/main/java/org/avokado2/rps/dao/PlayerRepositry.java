package org.avokado2.rps.dao;

import org.avokado2.rps.model.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlayerRepositry extends JpaRepository<PlayerEntity, Integer> {
    PlayerEntity findById(long id);
    List<PlayerEntity> findByLogin(String login);
}
