package pl.cezarysanecki.parkingdomain.commons.commands;

import lombok.Value;

import java.util.Set;

public interface Result {

    class Success implements Result {

    }

    @Value
    class Rejection implements Result {

        Set<ValidationError> validationErrors;

        public static Rejection empty() {
            return new Rejection(Set.of());
        }

        public static Rejection with(String message) {
            return new Rejection(Set.of(ValidationError.global(message)));
        }

    }

}
