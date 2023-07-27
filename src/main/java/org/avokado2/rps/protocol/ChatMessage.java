package org.avokado2.rps.protocol;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ChatMessage {
    private Date timestamp;

    private String login;

    private String message;

}
