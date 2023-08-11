package org.avokado2.rps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.exception.ManagerException;
import org.avokado2.rps.maneger.GameManager;
import org.avokado2.rps.protocol.ChatAddMessageRequest;
import org.avokado2.rps.protocol.EmptyResponse;
import org.avokado2.rps.protocol.ErrorResponse;
import org.avokado2.rps.protocol.GameStatus;
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
    public EmptyResponse startGame() {
        gameManager.startGameRequest(2);
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
