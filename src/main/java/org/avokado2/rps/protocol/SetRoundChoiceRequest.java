package org.avokado2.rps.protocol;

import lombok.Getter;
import lombok.Setter;
import org.avokado2.rps.model.GameChoice;

@Getter
@Setter
public class SetRoundChoiceRequest {

    private GameChoice choice;
}
