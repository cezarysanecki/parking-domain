package pl.cezarysanecki.parkingdomain.reservation.schedule.infrastructure;

import io.vavr.API;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationCancelled;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.reservation.schedule.model.ParkingSpotReservationsEvent.ReservationMade;

@Slf4j
class ParkingReservationsEntity {

    final UUID parkingSpotId;
    final Set<ReservationEntity> collection;

    ParkingReservationsEntity(UUID parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
        this.collection = new HashSet<>();
        this.noOccupation = true;
    }

    ParkingReservationsEntity handle(ParkingSpotReservationsEvent event) {
        return API.Match(event).of(
                Case($(instanceOf(ReservationMade.class)), this::handle),
                Case($(instanceOf(ReservationCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    ParkingReservationsEntity changeOccupation(boolean occupied) {
        this.noOccupation = !occupied;
        log.debug("parking spot with id {} is {}", parkingSpotId, occupied ? "occupied" : "free");
        return this;
    }

    private ParkingReservationsEntity handle(ReservationMade reservationMade) {
        collection.add(new ReservationEntity(
                reservationMade.getReservationId().getValue(),
                reservationMade.getClientId().getValue()));
        return this;
    }

    private ParkingReservationsEntity handle(ReservationCancelled reservationCancelled) {
        collection.removeIf(reservationEntity -> reservationEntity.reservationId.equals(reservationCancelled.getReservationId().getValue()));
        return this;
    }

}
