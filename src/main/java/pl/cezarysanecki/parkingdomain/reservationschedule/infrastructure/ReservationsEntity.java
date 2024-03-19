package pl.cezarysanecki.parkingdomain.reservationschedule.infrastructure;

import io.vavr.API;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationCancelled;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent.ReservationMade;

@Slf4j
class ReservationsEntity {

    final UUID parkingSpotId;
    final Set<ReservationEntity> collection;
    boolean noOccupation;

    ReservationsEntity(UUID parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
        this.collection = new HashSet<>();
        this.noOccupation = true;
    }

    ReservationsEntity handle(ReservationScheduleEvent event) {
        return API.Match(event).of(
                Case($(instanceOf(ReservationMade.class)), this::handle),
                Case($(instanceOf(ReservationCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    ReservationsEntity changeOccupation(boolean occupied) {
        this.noOccupation = !occupied;
        log.debug("parking spot with id {} is {}", parkingSpotId, occupied ? "occupied" : "free");
        return this;
    }

    private ReservationsEntity handle(ReservationMade reservationMade) {
        collection.add(new ReservationEntity(
                reservationMade.getReservationId().getValue(),
                reservationMade.getReservationSlot().getSince(),
                reservationMade.getReservationSlot().until(),
                reservationMade.getClientId().getValue()));
        return this;
    }

    private ReservationsEntity handle(ReservationCancelled reservationCancelled) {
        collection.removeIf(reservationEntity -> reservationEntity.reservationId.equals(reservationCancelled.getReservationId().getValue()));
        return this;
    }

}
