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

@RequiredArgsConstructor
public class ClientReservationRequestValidator {

    private final DateProvider dateProvider;

    public Set<ValidationError> validate(CreateReservationRequestForChosenParkingSpotCommand command) {
        return Stream.of(
                        requestDateMustBeInValidPeriod(command.getWhen()))
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<ValidationError> validate(CreateReservationRequestForPartOfAnyParkingSpotCommand command) {
        return Stream.of(
                        requestDateMustBeInValidPeriod(command.getWhen()))
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<ValidationError> validate(CancelReservationRequestCommand command) {
        return Stream.of(
                        requestDateMustBeInValidPeriod(command.getWhen()))
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Optional<ValidationError> requestDateMustBeInValidPeriod(LocalDateTime when) {
        LocalDateTime start = dateProvider.nearestFutureDateAt(LocalTime.of(4, 0));
        LocalDateTime end = dateProvider.nearestFutureDateAt(LocalTime.of(5, 0));

        if (start.isBefore(when) && end.isAfter(when)) {
            return Optional.of(new ValidationError("when", "time limitation"));
        }
        return Optional.empty();
    }

}
