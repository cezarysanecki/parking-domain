package pl.cezarysanecki.parkingdomain.client.reservationrequest.application;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.commands.ValidationError;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@RequiredArgsConstructor
public class ClientReservationRequestValidator {

    private final DateProvider dateProvider;

    public Set<ValidationError> validate(CreateReservationRequestForChosenParkingSpotCommand command) {
        LocalDateTime currentDateTime = dateProvider.now();
        LocalDate currentDate = currentDateTime.toLocalDate();
        LocalTime currentTime = currentDateTime.toLocalTime();

        LocalDateTime commandDateTime = command.getWhen();
        LocalDate commandDate = commandDateTime.toLocalDate();
        LocalTime commandTime = commandDateTime.toLocalTime();

//        if (curr)
        return Set.of();
    }

}
