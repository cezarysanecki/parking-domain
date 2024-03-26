package pl.cezarysanecki.parkingdomain.commons.commands;

import lombok.Value;

@Value
public class ValidationError {

    String field;
    String message;

}
