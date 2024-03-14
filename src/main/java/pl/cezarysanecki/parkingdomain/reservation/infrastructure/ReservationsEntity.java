package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import io.vavr.API;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleParked;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationCancelled;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.CompletelyFreedUp;
import static pl.cezarysanecki.parkingdomain.reservation.model.ReservationEvent.ReservationMade;

class ReservationsEntity {

    final UUID parkingSpotId;
    final Set<ReservationEntity> collection;
    boolean free;

    ReservationsEntity(UUID parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
        this.collection = new HashSet<>();
        this.free = true;
    }

    ReservationsEntity handle(ReservationEvent event) {
        return API.Match(event).of(
                Case($(instanceOf(ReservationMade.class)), this::handle),
                Case($(instanceOf(ReservationCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    ReservationsEntity handle(ParkingSpotEvent event) {
        return API.Match(event).of(
                Case($(instanceOf(VehicleParked.class)), this::handle),
                Case($(instanceOf(CompletelyFreedUp.class)), this::handle),
                Case($(), () -> this));
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

    private ReservationsEntity handle(VehicleParked vehicleParked) {
        this.free = false;
        return this;
    }

    private ReservationsEntity handle(CompletelyFreedUp completelyFreedUp) {
        this.free = true;
        return this;
    }

}
