package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.commands.ValidationError;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ClientReservationRequestCommandValidator {

    Set<ValidationError> validate(ClientReservationRequestCommand command);

    @RequiredArgsConstructor
    class Production implements ClientReservationRequestCommandValidator {

        private final DateProvider dateProvider;

        @Override
        public Set<ValidationError> validate(ClientReservationRequestCommand command) {
            return Stream.of(
                            requestDateMustBeInValidPeriod(command.getWhen()))
                    .flatMap(Optional::stream)
                    .collect(Collectors.toUnmodifiableSet());
        }

        private Optional<ValidationError> requestDateMustBeInValidPeriod(LocalDateTime when) {
            LocalDateTime end = dateProvider.nearestFutureDateAt(LocalTime.of(5, 0));
            LocalDateTime start = end.minusHours(1);

            if (start.isBefore(when) && when.isBefore(end)) {
                return Optional.of(new ValidationError("when", "time limitation"));
            }
            return Optional.empty();
        }

    }

}
