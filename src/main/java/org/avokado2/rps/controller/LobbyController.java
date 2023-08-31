package org.avokado2.rps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.maneger.PlayerManager;
import org.avokado2.rps.protocol.ChatMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Controller
public class LobbyController {

    private final PlayerManager playerManager;

    @RequestMapping(value = "/lobby", method = RequestMethod.GET)
    public ModelAndView openRegister() {
        Map<String,Object> model = new HashMap<>();
        model.put("currentNickname", playerManager.getCurrentPlayer().getLogin());
        return new ModelAndView("lobby", model);
    }
}
