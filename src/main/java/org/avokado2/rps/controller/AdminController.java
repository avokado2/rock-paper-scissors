package org.avokado2.rps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.maneger.PlayerManager;
import org.avokado2.rps.protocol.BlockPlayerRequest;
import org.avokado2.rps.protocol.EmptyResponse;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PlayerManager playerManager;

    @ResponseBody
    @PostMapping("/block-player")
    public EmptyResponse blockPlayer (@RequestBody BlockPlayerRequest request) {
        playerManager.blockPlayer(request.getNickname());
        return new EmptyResponse();
    }
}
