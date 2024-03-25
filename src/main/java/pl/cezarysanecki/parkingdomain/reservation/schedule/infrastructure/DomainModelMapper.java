package pl.cezarysanecki.parkingdomain.reservation.schedule.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.DayPartReservations;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.Reservation;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationPeriod;

import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpotReservations map(ParkingReservationsEntity entity) {
        return new ParkingSpotReservations(
                ParkingSpotId.of(entity.parkingSpotId),
                entity.collection.stream()
                        .map(reservationEntity -> reservationEntity.dayParts.stream()
                                .collect(Collectors.toMap(dayPart -> dayPart, dayPart -> reservationEntity)))
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> map(entry.getKey(), entity)
                        )));
    }

    private static DayPartReservations map(ReservationPeriod.DayPart dayPart, ParkingReservationsEntity entity) {
        return new DayPartReservations(
                dayPart,
                entity.collection.stream()
                        .map(reservationEntity -> {
                            if (reservationEntity.type == ReservationEntity.Type.Individual) {
                                return new Reservation.Individual(ReservationId.of(reservationEntity.reservationId));
                            }
                            return new Reservation.Collective(ReservationId.of(reservationEntity.reservationId));
                        })
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
