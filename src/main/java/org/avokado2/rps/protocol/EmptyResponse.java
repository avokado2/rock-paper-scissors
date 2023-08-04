package org.avokado2.rps.protocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmptyResponse {
    private String status = "ok";
}
