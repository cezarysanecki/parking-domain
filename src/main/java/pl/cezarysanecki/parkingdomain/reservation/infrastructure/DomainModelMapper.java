package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservation;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.reservation.model.Reservation;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationPeriod;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpotReservations map(ParkingReservationsEntity entity) {
        return ParkingSpotReservations.of(
                ParkingSpotId.of(entity.parkingSpotId),
                entity.collection.stream()
                        .map(DomainModelMapper::map)
                        .collect(Collectors.toUnmodifiableSet()));
    }

    private static ParkingSpotReservation map(ReservationEntity reservationEntity) {
        Reservation reservation;
        if (reservationEntity.type == ReservationEntity.Type.Individual) {
            reservation = new Reservation.Individual(ReservationId.of(reservationEntity.reservationId));
        } else {
            reservation = new Reservation.Collective(ReservationId.of(reservationEntity.reservationId));
        }
        return new ParkingSpotReservation(new ReservationPeriod(reservationEntity.dayParts), reservation);
    }

}
