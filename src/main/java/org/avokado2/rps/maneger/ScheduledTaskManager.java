package org.avokado2.rps.maneger;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.avokado2.rps.dao.GameRepositry;
import org.avokado2.rps.dao.GameRequestRepository;
import org.avokado2.rps.model.GameEntity;
import org.avokado2.rps.model.GameRequestEntity;
import org.avokado2.rps.model.PlayerEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTaskManager {

    private final GameManager gameManager;

    private final GameRequestRepository gameRequestRepository;

    private final GameRepositry gameRepositry;

    private final SettingManager settingManager;


    @Scheduled(fixedDelay = 5000)
    public void createGamesJob() {
        List<GameRequestEntity> gameRequestEntities = gameManager.getPendingRequest();
        //разбиение на пары
        List<Pair<GameRequestEntity, GameRequestEntity>> pairList = new ArrayList<>();
        for (int i = 0; i < gameRequestEntities.size() - 1; i += 2) {
            Pair<GameRequestEntity, GameRequestEntity> pair = Pair.of(gameRequestEntities.get(i), gameRequestEntities.get(i + 1));
            pairList.add(pair);
            long leftId = pair.getLeft().getId();
            long rightId = pair.getRight().getId();
            gameManager.createGame(leftId, rightId, 3);
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void startNextRoundJob() {
        List<GameEntity> gameEntityList = gameRepositry.findByPause(true);
        for (GameEntity g: gameEntityList) {
            if (System.currentTimeMillis() > g.getUpdateAt().getTime() + settingManager.getRoundPauseMs()){
                gameManager.startNewRound(g.getId());
            }
        }

    }
}
