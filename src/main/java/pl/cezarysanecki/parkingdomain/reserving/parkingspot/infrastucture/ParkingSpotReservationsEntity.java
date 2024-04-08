package pl.cezarysanecki.parkingdomain.reserving.parkingspot.infrastucture;

import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.ParkingSpotReservationCancelled;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.PartOfParkingSpotReserved;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent.WholeParkingSpotReserved;

@AllArgsConstructor
class ParkingSpotReservationsEntity {

    UUID parkingSpotId;
    int capacity;
    Set<VehicleReservationEntity> reservations;

    ParkingSpotReservationsEntity handle(ParkingSpotReservationEvent domainEvent) {
        return Match(domainEvent).of(
                API.Case(API.$(instanceOf(PartOfParkingSpotReserved.class)), this::handle),
                API.Case(API.$(instanceOf(WholeParkingSpotReserved.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotReservationCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotReservationsEntity handle(PartOfParkingSpotReserved partOfParkingSpotReserved) {
        reservations.add(new VehicleReservationEntity(
                partOfParkingSpotReserved.getReservationId().getValue(),
                partOfParkingSpotReserved.getVehicleSize().getValue()));
        return this;
    }

    private ParkingSpotReservationsEntity handle(WholeParkingSpotReserved wholeParkingSpotReserved) {
        reservations.add(new VehicleReservationEntity(
                wholeParkingSpotReserved.getReservationId().getValue(),
                capacity));
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
