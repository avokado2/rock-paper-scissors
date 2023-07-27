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
        game.setRoundsCount(roundsCount);

        // Create first round with null values for choice1 and choice2
        GameRoundEntity round = new GameRoundEntity();
        round.setGame(game);
        round.setRoundNumber(1);
        round.setChoice1(null);
        round.setChoice2(null);
        gameRoundRepository.save(round);

        gameRepositry.save(game);
        gameRequestRepository.delete(gameRequest1.get());
        gameRequestRepository.delete(gameRequest2.get());
        return true;
    }

    @Transactional
    public void playRound(long gameId, GameChoice choicePlayer1, GameChoice choicePlayer2) {
        int winner = pickAWinner(choicePlayer1, choicePlayer2);

        Optional<GameEntity> optionalGame = gameRepositry.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new RuntimeException("game not found");
        }
        GameEntity game = optionalGame.get();
        if (game.isCompleted()) {
            throw new RuntimeException("Game is already completed");
        }
        if (winner == 1){
            game.setScore1(game.getScore1() + 1);
        } else if (winner == 2){
            game.setScore2(game.getScore2() + 1);
        }
        game.setCurrentRound(game.getCurrentRound() + 1);
        if (game.getCurrentRound() == game.getRoundsCount()) {
            game.setCompleted(true);
        }

        gameRepositry.save(game);

        GameRoundEntity gameRound = new GameRoundEntity();
        gameRound.setGame(game);
        gameRound.setRoundNumber(game.getCurrentRound());
        gameRound.setChoice1(choicePlayer1);
        gameRound.setChoice2(choicePlayer2);
        gameRoundRepository.save(gameRound);

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
        List<GameEntity> gameEntities1 = gameRepositry.findByPlayer1(playerE, false);
        List<GameEntity> gameEntities2 = gameRepositry.findByPlayer2(playerE, false);
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
        return true;
    }

    public void cancelGameRequest() {
        PlayerEntity playerE = playerRepositry.getReferenceById(playerManager.getCurrentPlayerId());
        List<GameRequestEntity> gameRequests = gameRequestRepository.findByPlayer(playerE);

        if (gameRequests.size() > 0){
            gameRequestRepository.delete(gameRequests.get(0));
        }
    }

}
