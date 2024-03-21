package pl.cezarysanecki.parkingdomain.reservation.schedule.infrastructure;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.date.DateProvider;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.Reservation;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.Reservations;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSchedule;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot;

import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class DomainModelMapper {

    private final DateProvider dateProvider;

    ReservationSchedule map(ReservationsEntity entity) {
        return new ReservationSchedule(
                ParkingSpotId.of(entity.parkingSpotId),
                new Reservations(
                        entity.collection.stream()
                                .map(reservationEntity -> new Reservation(
                                        ReservationId.of(reservationEntity.reservationId),
                                        new ReservationSlot(reservationEntity.since, (int) ChronoUnit.HOURS.between(reservationEntity.since, reservationEntity.until)),
                                        ClientId.of(reservationEntity.clientId)
                                ))
                                .collect(Collectors.toUnmodifiableSet())),
                entity.noOccupation,
                dateProvider.now());
    }

}
