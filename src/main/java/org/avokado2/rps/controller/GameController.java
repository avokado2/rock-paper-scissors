package org.avokado2.rps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.exception.ManagerException;
import org.avokado2.rps.maneger.GameManager;
import org.avokado2.rps.protocol.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/game")
public class GameController {

    private final GameManager gameManager;

    @ResponseBody
    @GetMapping("/get-status")
    public GameStatus getStatus() {
        return gameManager.getGameStatus();
    }


    @ResponseBody
    @PostMapping("/start-game")
    public EmptyResponse startGame(@RequestBody StartGameRequest request ) {
        int numberOfPlayers = request.getNumberOfPlayers();
        if(numberOfPlayers != 2 && numberOfPlayers != 3) {
            throw new RuntimeException("incorrect value of players! Expected 2 or 3");
        }
        gameManager.startGameRequest(numberOfPlayers);
        return new EmptyResponse();
    }

    @ResponseBody
    @PostMapping("/set-round-choice")
    public EmptyResponse setRoundChoice(@RequestBody SetRoundChoiceRequest choiceRequest ) {
        gameManager.setRoundChoice(choiceRequest.getChoice());
        return new EmptyResponse();
    }


    @ResponseBody
    @PostMapping("/cancel-game")
    public EmptyResponse cancelGame() {
        gameManager.cancelGameRequest();
        return new EmptyResponse();
    }

    @ResponseBody
    @ExceptionHandler(ManagerException.class)
    public ErrorResponse onManagerException(ManagerException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(ex.getMessage());
        return errorResponse;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ErrorResponse onException(Exception ex) {
        log.error("Internal error occurred", ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage("Internal error occurred");
        return errorResponse;
    }
}
