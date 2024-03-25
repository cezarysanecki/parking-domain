package pl.cezarysanecki.parkingdomain.reservation.schedule.infrastructure;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.Reservation;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;

import java.util.stream.Collectors;

@RequiredArgsConstructor
class DomainModelMapper {

    private final DateProvider dateProvider;

    ReservationSchedule map(ParkingReservationsEntity entity) {
        return new ReservationSchedule(
                ParkingSpotId.of(entity.parkingSpotId),
                new ParkingSpotReservations(
                        entity.collection.stream()
                                .map(reservationEntity -> new Reservation(
                                        ReservationId.of(reservationEntity.reservationId),
                                        ClientId.of(reservationEntity.clientId)
                                ))
                                .collect(Collectors.toUnmodifiableSet())),
                entity.noOccupation,
                dateProvider.now());
    }

}
