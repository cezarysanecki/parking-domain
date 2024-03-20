package pl.cezarysanecki.parkingdomain.clientreservations.infrastructure;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservations;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DomainModelMapper {

    private final DateProvider dateProvider;

    ClientReservations map(ClientReservationsEntity entity) {
        return new ClientReservations(
                ClientId.of(entity.clientId),
                entity.reservations.stream()
                        .map(ReservationId::of)
                        .collect(Collectors.toUnmodifiableSet()),
                dateProvider.now());
    }

}
