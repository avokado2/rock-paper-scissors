package org.avokado2.rps.protocol;

import lombok.Getter;
import lombok.Setter;
import org.avokado2.rps.model.GameChoice;

@Getter
@Setter
public class GameStatus {

    private GameStatusType type;

    private int currentRound;

    private int roundsCount;

    private GameChoice selfChoice;

    private GameChoice enemyChoice;

    private Boolean winner;
}
