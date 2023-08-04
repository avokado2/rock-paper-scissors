package org.avokado2.rps.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewChatMessageEvent {
    private String nickname;

    private String text;

    private long gameId;
}
