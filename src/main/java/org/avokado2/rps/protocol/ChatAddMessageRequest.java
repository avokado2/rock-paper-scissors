package org.avokado2.rps.protocol;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
public class ChatAddMessageRequest {

    private long gameId;

    private String message;

    private String toNickname;
}
