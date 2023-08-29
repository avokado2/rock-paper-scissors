package org.avokado2.rps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.exception.ManagerException;
import org.avokado2.rps.maneger.ChatMessageManager;
import org.avokado2.rps.protocol.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatMessageManager chatMessageManager;


    @ResponseBody
    @PostMapping("/add-message")
    public EmptyResponse addMessage(@RequestBody ChatAddMessageRequest request) {
        chatMessageManager.addMessage(request.getGameId(), request.getMessage(), request.getToNickname());
        //playerManager.createPlayer();
        return new EmptyResponse();
    }

    @ResponseBody
    @GetMapping("/get-messages")
    public List<ChatMessage> getMessages(@RequestParam(value = "gameId", defaultValue = "0") long gameId) {
        return chatMessageManager.getMessages(gameId);

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
