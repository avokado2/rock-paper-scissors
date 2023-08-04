package org.avokado2.rps.maneger;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.avokado2.rps.dao.GameRequestRepository;
import org.avokado2.rps.model.GameRequestEntity;
import org.avokado2.rps.model.PlayerEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTaskManager {

    private final GameManager gameManager;

    private final GameRequestRepository gameRequestRepository;


    // @Scheduled(fixedDelay = 5000)
    public void createGamesJob() {
        //получение всех gameRequest
        List<GameRequestEntity> gameRequestEntities = gameRequestRepository.findAll();
        //удаление запросов с тремя игроками
        gameRequestEntities.removeIf(request -> request.getNumberOfPlayers() == 3);
        //сортировка по рейтингу игроков
        gameRequestEntities.sort(Comparator.comparingInt(request -> request.getPlayer().getRating()));
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
}