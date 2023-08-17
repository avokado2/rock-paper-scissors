package org.avokado2.rps.protocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartGameRequest {

    private int numberOfPlayers;
}
