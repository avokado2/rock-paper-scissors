package org.avokado2.rps.maneger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.dao.SettingRepository;
import org.avokado2.rps.model.SettingEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SettingManager {
    private final SettingRepository settingRepository;

    @Cacheable(value = "setting", key = "'chat_min_message_interval_ms'")
    public long getMinMessageIntervalMs(){
        SettingEntity setting = settingRepository.findByName("chat_min_message_interval_ms");
        if (setting != null) {
            return Long.parseLong(setting.getValue());
        } else {
            return 0;
        }
    }

    @Cacheable(value = "setting", key = "'game_round_pause_ms'")
    public long getRoundPauseMs(){
        SettingEntity setting = settingRepository.findByName("game_round_pause_ms");
        if (setting != null) {
            return Long.parseLong(setting.getValue());
        } else {
            return 0;
        }
    }
    @CacheEvict(value = "setting", allEntries = true)
    public void invalidateAll() {


    }

    @Cacheable(value = "setting", key = "'game_round_pause_ms'")
    public long getRoundCompletedPauseMs(){
        SettingEntity setting = settingRepository.findByName("game_round_completed_pause_ms");
        if (setting != null) {
            return Long.parseLong(setting.getValue());
        } else {
            return 0;
        }
    }
}
