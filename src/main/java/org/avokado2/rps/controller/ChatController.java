package org.avokado2.rps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.exception.ManagerException;
import org.avokado2.rps.maneger.ChatMessageManager;
import org.avokado2.rps.protocol.ChatAddMessageRequest;
import org.avokado2.rps.protocol.EmptyResponse;
import org.avokado2.rps.protocol.ErrorResponse;
import org.avokado2.rps.protocol.HelloResponse;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatMessageManager chatMessageManager;

    @ResponseBody
    @PostMapping("/add-message")
    public EmptyResponse addMessage(@RequestBody ChatAddMessageRequest request) {
        chatMessageManager.addMessage(request.getGameId(), request.getMessage());
        //playerManager.createPlayer();
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