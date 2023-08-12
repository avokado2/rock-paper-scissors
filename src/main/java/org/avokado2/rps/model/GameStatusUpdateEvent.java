package org.avokado2.rps.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameStatusUpdateEvent {

    private String login;
}
