package org.avokado2.rps.controller;

import lombok.RequiredArgsConstructor;
import org.avokado2.rps.maneger.ChatMessageManager;
import org.avokado2.rps.maneger.GameManager;
import org.avokado2.rps.maneger.PlayerManager;
import org.avokado2.rps.maneger.ScheduledTaskManager;
import org.avokado2.rps.model.ChatMessageEntity;
import org.avokado2.rps.model.GameChoice;
import org.avokado2.rps.model.PlayerEntity;
import org.avokado2.rps.protocol.ChatMessage;
import org.avokado2.rps.protocol.HelloResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;


/*
контроллер это компонет спринг приложения который обрабатывает входящие http запросы
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class TestController {
    private final PlayerManager playerManager;

    private final ChatMessageManager chatMessageManager;

    private final GameManager gameManager;

    private final ScheduledTaskManager taskManager;

    @Autowired
    private  PasswordEncoder passwordEncoder;
    @ResponseBody
    @GetMapping("/hello")
    public HelloResponse hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        //playerManager.createPlayer();
        String ret = String.format("Hello %s! PasHash %s", name, passwordEncoder.encode(name));
        return new HelloResponse(ret);
    }
    @ResponseBody
    @GetMapping("/add-message")
    public HelloResponse addMessage(@RequestParam(value = "gameId", defaultValue = "0") long gameId,
                                    @RequestParam(value = "message", defaultValue = "hello") String message) {
        chatMessageManager.addMessage(gameId, message);
        //playerManager.createPlayer();
        String ret = String.format("Hello %s! PasHash %s", message, passwordEncoder.encode(message));
        return new HelloResponse(ret);
    }
    @ResponseBody
    @GetMapping("/get-messages")
    public List<ChatMessage> getMessages(@RequestParam(value = "gameId", defaultValue = "0") long gameId) {
        List<ChatMessageEntity> messageEntities = chatMessageManager.getMessages(gameId);
        List<ChatMessage> messages = new ArrayList<>();
        for (ChatMessageEntity entity : messageEntities) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(entity.getMessage());
            chatMessage.setTimestamp(entity.getTimestamp());
            chatMessage.setLogin(entity.getPlayer().getLogin());
            messages.add(chatMessage);
        }
        return messages;
    }


    @ResponseBody
    @GetMapping("/start-game")
    public HelloResponse startGameRequest(@RequestParam(value = "numberOfPlayers") int numberOfPlayers) {
        if(numberOfPlayers != 2 && numberOfPlayers != 3) {
            throw new RuntimeException("incorrect value of players! Expected 2 or 3");
        }

        gameManager.startGameRequest(numberOfPlayers);
        String ret = String.format("Hello !");
        return new HelloResponse(ret);
    }
    @ResponseBody
    @GetMapping("/start-game-job")
    public HelloResponse startGameJob() {
        taskManager.createGamesJob();
        String ret = String.format("Hello !");
        return new HelloResponse(ret);
    }
    @ResponseBody
    @GetMapping("/cancel-game-request")
    public HelloResponse cancelGameRequest() {
        gameManager.cancelGameRequest();
        String ret = String.format("Hello !");
        return new HelloResponse(ret);
    }
    @ResponseBody
    @GetMapping("/set-round-choice")
    public HelloResponse setRoundChoice(@RequestParam(value = "gameChoice") GameChoice gameChoice) {
        gameManager.setRoundChoice(gameChoice);
        String ret = String.format("Hello !");
        return new HelloResponse(ret);
    }
}
