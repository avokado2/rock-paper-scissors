package org.avokado2.rps.dao;

import org.avokado2.rps.model.PlayerEntity;
import org.avokado2.rps.model.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<SettingEntity, Integer> {

    SettingEntity findByName(String name);
}
