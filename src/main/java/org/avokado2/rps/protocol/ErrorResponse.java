package org.avokado2.rps.protocol;

import lombok.Getter;
import lombok.Setter;
import org.avokado2.rps.exception.ManagerException;

@Getter
@Setter
public class ErrorResponse {
    private String errorMessage;
}
