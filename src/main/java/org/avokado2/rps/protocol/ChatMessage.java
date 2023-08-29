package org.avokado2.rps.protocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String nickname;

    private String text;

    private long gameId;

    private long id;

    private boolean privateMessage;

    private String recipient;
}
