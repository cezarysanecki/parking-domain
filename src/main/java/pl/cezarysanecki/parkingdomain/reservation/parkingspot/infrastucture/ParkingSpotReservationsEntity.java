package pl.cezarysanecki.parkingdomain.reservation.parkingspot.infrastucture;

import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancelled;
import pl.cezarysanecki.parkingdomain.reservation.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReserved;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@AllArgsConstructor
class ParkingSpotReservationsEntity {

    UUID parkingSpotId;
    int capacity;
    Set<VehicleReservationEntity> reservations;

    ParkingSpotReservationsEntity handle(ParkingSpotReservationEvent domainEvent) {
        return Match(domainEvent).of(
                API.Case(API.$(instanceOf(ParkingSpotReserved.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotReservationCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotReservationsEntity handle(ParkingSpotReserved parkingSpotReserved) {
        reservations.add(new VehicleReservationEntity(
                parkingSpotReserved.getReservationId().getValue(),
                parkingSpotReserved.getVehicleSize().getValue()));
        return this;
    }

    private ParkingSpotReservationsEntity handle(ParkingSpotReservationCancelled parkingSpotReservationCancelled) {
        reservations.removeIf(reservation -> reservation.reservationId.equals(parkingSpotReservationCancelled.getReservationId().getValue()));
        return this;
    }

    @AllArgsConstructor
    static class VehicleReservationEntity {

        @NonNull UUID reservationId;
        @NonNull int size;

    }

}
