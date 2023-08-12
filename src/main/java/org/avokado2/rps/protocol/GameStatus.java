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

    private int selfScore;

    private int enemyScore;

    private String selfNickname;

    private String enemyNickname;

    private int selfRating;

    private int enemyRating;
}
