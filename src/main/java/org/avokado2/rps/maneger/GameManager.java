package org.avokado2.rps.maneger;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.avokado2.rps.dao.GameRepositry;
import org.avokado2.rps.dao.GameRequestRepository;
import org.avokado2.rps.dao.GameRoundRepository;
import org.avokado2.rps.dao.PlayerRepositry;
import org.avokado2.rps.model.*;
import org.avokado2.rps.protocol.GameStatus;
import org.avokado2.rps.protocol.GameStatusType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameManager {

     private final GameRepositry gameRepositry;

     private final PlayerRepositry playerRepositry;

     private final GameRoundRepository gameRoundRepository;

     private final GameRequestRepository gameRequestRepository;

     private final PlayerManager playerManager;

     private final ApplicationEventPublisher publisher;


    @Transactional
    public boolean createGame(long gameRequestId1, long gameRequestId2, int roundsCount ) {
        Optional<GameRequestEntity> gameRequest1 = gameRequestRepository.findById(gameRequestId1);
        Optional<GameRequestEntity> gameRequest2 = gameRequestRepository.findById(gameRequestId2);
        if (gameRequest1.isEmpty() || gameRequest2.isEmpty()) {
            return false;
        }
        GameEntity game = new GameEntity();
        PlayerEntity playerE1 = gameRequest1.get().getPlayer();
        game.setPlayer1(playerE1);
        PlayerEntity playerE2 = gameRequest2.get().getPlayer();
        game.setPlayer2(playerE2);
        game.setCurrentRound(1);
        game.setRoundsCount(roundsCount);

        gameRepositry.save(game);
        GameRoundEntity round = new GameRoundEntity();
        round.setGame(game);
        round.setRoundNumber(1);
        round.setChoice1(null);
        round.setChoice2(null);
        gameRoundRepository.save(round);

        gameRequestRepository.delete(gameRequest1.get());
        gameRequestRepository.delete(gameRequest2.get());
        publisher.publishEvent(new GameStatusUpdateEvent(playerE1.getLogin()));
        publisher.publishEvent(new GameStatusUpdateEvent(playerE2.getLogin()));
        return true;
    }


    private void playRound(GameEntity game, GameRoundEntity gameRound) {
        int winner = pickAWinner(gameRound.getChoice1(), gameRound.getChoice2());
        gameRound.setWinner(winner);
        gameRoundRepository.save(gameRound);
        game.setPause(true);

        if (game.isCompleted()) {
            throw new RuntimeException("Game is already completed");
        }
        if (winner == 1){
            game.setScore1(game.getScore1() + 1);
        } else if (winner == 2){
            game.setScore2(game.getScore2() + 1);
        }
        gameRepositry.save(game);

    }

    @Transactional
    public void startNewRound(long gameId){
        GameEntity game = gameRepositry.getById(gameId);
        game.setCurrentRound(game.getCurrentRound() + 1);
        int roundC = game.getRoundsCount() + 1;
        if (game.getCurrentRound() == roundC) {
            game.setCompleted(true);
        }
        if (game.isCompleted()){
            game.setCurrentRound(game.getRoundsCount());
        }
        game.setPause(false);
        gameRepositry.save(game);

        if (game.isCompleted() && game.getScore1() != game.getScore2()) {
            Optional<PlayerEntity> playerEntity1 = playerRepositry.findById(game.getPlayer1().getId());
            Optional<PlayerEntity> playerEntity2 = playerRepositry.findById(game.getPlayer2().getId());
            PlayerEntity player1 = playerEntity1.get();
            PlayerEntity player2 = playerEntity2.get();
            if (game.getScore1() > game.getScore2()) {
                player1.setRating(player1.getRating() + 5);
                player2.setRating(player2.getRating() - 5);
            } else {
                player1.setRating(player1.getRating() - 5);
                player2.setRating(player2.getRating() + 5);
            }
            playerRepositry.save(player1);
            playerRepositry.save(player2);
        }
        if (!game.isCompleted()) {
            GameRoundEntity gameRoundEntity = new GameRoundEntity();
            gameRoundEntity.setGame(game);
            gameRoundEntity.setRoundNumber(game.getCurrentRound());
            gameRoundRepository.save(gameRoundEntity);
        }
        publisher.publishEvent(new GameStatusUpdateEvent(game.getPlayer1().getLogin()));
        publisher.publishEvent(new GameStatusUpdateEvent(game.getPlayer2().getLogin()));
    }
    private int pickAWinner (GameChoice choicePlayer1, GameChoice choicePlayer2) {
        int scorePlayer1 = 0;
        int scorePlayer2 = 0;

        if (choicePlayer1 == GameChoice.rock) {
            if (choicePlayer2 == GameChoice.scissors) {
                scorePlayer1++;
            } else if (choicePlayer2 == GameChoice.paper) {
                scorePlayer2++;
            }
        } else if (choicePlayer1 == GameChoice.scissors) {
            if (choicePlayer2 == GameChoice.paper) {
                scorePlayer1++;
            } else if (choicePlayer2 == GameChoice.rock) {
                scorePlayer2++;
            }
        } else if (choicePlayer1 == GameChoice.paper) {
            if (choicePlayer2 == GameChoice.rock) {
                scorePlayer1++;
            } else if (choicePlayer2 == GameChoice.scissors) {
                scorePlayer2++;
            }
        }
        if (scorePlayer1 == 0 && scorePlayer2 == 0){
            return 0;
        }else if (scorePlayer1 > scorePlayer2){
            return 1;
        }
        return 2;
    }

    @Transactional
    public boolean startGameRequest(int numberOfPlayers) {
        PlayerEntity playerE = playerRepositry.getReferenceById(playerManager.getCurrentPlayerId());
        List<GameRequestEntity> gameRequests = gameRequestRepository.findByPlayer(playerE);
        List<GameEntity> gameEntities1 = gameRepositry.findByPlayer1AndCompleted(playerE, false);
        List<GameEntity> gameEntities2 = gameRepositry.findByPlayer2AndCompleted(playerE, false);
        if (!gameEntities1.isEmpty()){
            return false;
        }
        if (!gameEntities2.isEmpty()){
            return false;
        }

        if (gameRequests.size() > 0){
            return false;
        }
        GameRequestEntity gameRequest = new GameRequestEntity();
        gameRequest.setNumberOfPlayers(numberOfPlayers);
        gameRequest.setPlayer(playerE);
        gameRequestRepository.save(gameRequest);
        publisher.publishEvent(new GameStatusUpdateEvent(playerE.getLogin()));
        return true;
    }
    @Transactional
    public void cancelGameRequest() {
        PlayerEntity playerE = playerRepositry.getReferenceById(playerManager.getCurrentPlayerId());
        List<GameRequestEntity> gameRequests = gameRequestRepository.findByPlayer(playerE);

        if (gameRequests.size() > 0){
            gameRequestRepository.delete(gameRequests.get(0));
        }
        publisher.publishEvent(new GameStatusUpdateEvent(playerE.getLogin()));
    }
    @Transactional
    public List<GameRequestEntity> getPendingRequest() {
        List<GameRequestEntity> gameRequestEntities = gameRequestRepository.findAll();
        //удаление запросов с тремя игроками
        gameRequestEntities.removeIf(request -> request.getNumberOfPlayers() == 3);
        //сортировка по рейтингу игроков
        gameRequestEntities.sort(Comparator.comparingInt(request -> request.getPlayer().getRating()));

        return gameRequestEntities;
    }

    @Transactional
    public void setRoundChoice(GameChoice gameChoice) {
        PlayerEntity currentPlayer = playerRepositry.getReferenceById(playerManager.getCurrentPlayerId());
        List<GameEntity> gameEntities1 = gameRepositry.findByPlayer1AndCompleted(currentPlayer, false);
        GameEntity currentGame ;
        if (!gameEntities1.isEmpty()){
            currentGame = gameEntities1.get(0);
        } else {
            List<GameEntity> gameEntities2 = gameRepositry.findByPlayer2AndCompleted(currentPlayer, false);
            if (!gameEntities2.isEmpty()) {
                currentGame = gameEntities2.get(0);
            } else {
                return;
            }
        }
        GameRoundEntity currentRound = gameRoundRepository.findByGameAndRoundNumber(currentGame, currentGame.getCurrentRound());

        if (currentPlayer.getId().equals(currentGame.getPlayer1().getId())) {
            if (currentRound.getChoice1() == null ) {
                currentRound.setChoice1(gameChoice);
            } else {
                return;
            }
        } else {
            if (currentRound.getChoice2() == null ) {
                currentRound.setChoice2(gameChoice);
            } else {
                return;
            }

        }
        gameRoundRepository.save(currentRound);

        if (currentRound.getChoice1() != null && currentRound.getChoice2() != null) {
            playRound(currentGame, currentRound);
        }
        publisher.publishEvent(new GameStatusUpdateEvent(currentGame.getPlayer1().getLogin()));
        publisher.publishEvent(new GameStatusUpdateEvent(currentGame.getPlayer2().getLogin()));
    }

    public GameStatus getGameStatus() {

        GameEntity gameEntity = getCurrentGame();
        PlayerEntity playerE = playerRepositry.getById(playerManager.getCurrentPlayerId());
        GameStatus gameStatus = new GameStatus();
        gameStatus.setSelfNickname(playerE.getLogin());
        gameStatus.setSelfRating(playerE.getRating());
        if (gameEntity != null){
            int playerId = playerManager.getCurrentPlayerId();
            boolean firstPlayer = gameEntity.getPlayer1().getId() == playerId;
            gameStatus.setType(GameStatusType.running);
            gameStatus.setCurrentRound(gameEntity.getCurrentRound());
            gameStatus.setRoundsCount(gameEntity.getRoundsCount());
            GameRoundEntity gameRound = gameRoundRepository.findByGameAndRoundNumber(gameEntity, gameEntity.getCurrentRound());
            if (firstPlayer) {
                gameStatus.setSelfChoice(gameRound.getChoice1());
                if (gameStatus.getSelfChoice() != null) {
                    gameStatus.setEnemyChoice(gameRound.getChoice2());
                }
                gameStatus.setSelfScore(gameEntity.getScore1());
                gameStatus.setEnemyScore(gameEntity.getScore2());
                gameStatus.setEnemyNickname(gameEntity.getPlayer2().getLogin());
                gameStatus.setEnemyRating(gameEntity.getPlayer2().getRating());
                if (gameRound.getWinner() != null) {
                    gameStatus.setWinner(gameRound.getWinner() == 1);
                    gameStatus.setEnemyWinner(gameRound.getWinner() == 2);
                }
            } else {
                gameStatus.setSelfChoice(gameRound.getChoice2());
                if (gameStatus.getSelfChoice() != null) {
                    gameStatus.setEnemyChoice(gameRound.getChoice1());
                }
                gameStatus.setSelfScore(gameEntity.getScore2());
                gameStatus.setEnemyScore(gameEntity.getScore1());
                gameStatus.setEnemyNickname(gameEntity.getPlayer1().getLogin());
                gameStatus.setEnemyRating(gameEntity.getPlayer1().getRating());
                if (gameRound.getWinner() != null) {
                    gameStatus.setWinner(gameRound.getWinner() == 2);
                    gameStatus.setEnemyWinner(gameRound.getWinner() == 1);
                }
            }
        } else {
            List<GameRequestEntity> gameRequest = gameRequestRepository.findByPlayer(playerE);
            if (gameRequest.isEmpty()) {
                gameStatus.setType(GameStatusType.init);
            } else {
                gameStatus.setType(GameStatusType.waitForGame);
            }
        }
        return gameStatus;
    }

    private GameEntity getCurrentGame() {

        PlayerEntity playerE = playerRepositry.getReferenceById(playerManager.getCurrentPlayerId());
        List<GameEntity> gameEntities1 = gameRepositry.findByPlayer1AndCompleted(playerE, false);
        List<GameEntity> gameEntities2 = gameRepositry.findByPlayer2AndCompleted(playerE, false);
        if (!gameEntities1.isEmpty()){
            return gameEntities1.get(0);
        }
        if (!gameEntities2.isEmpty()){
            return gameEntities2.get(0);
        } else {
            return null;
        }
    }
}
