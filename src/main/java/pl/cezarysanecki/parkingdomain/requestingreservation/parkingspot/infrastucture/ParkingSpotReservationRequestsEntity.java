package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastucture;

import io.vavr.API;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.PartRequestOfParkingSpotReserved;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.WholeRequestParkingSpotReserved;

@AllArgsConstructor
class ParkingSpotReservationRequestsEntity {

    UUID parkingSpotId;
    int capacity;
    Set<VehicleReservationEntity> reservations;

    ParkingSpotReservationRequestsEntity handle(ParkingSpotReservationRequestEvent domainEvent) {
        return Match(domainEvent).of(
                API.Case(API.$(instanceOf(PartRequestOfParkingSpotReserved.class)), this::handle),
                API.Case(API.$(instanceOf(WholeRequestParkingSpotReserved.class)), this::handle),
                API.Case(API.$(instanceOf(ParkingSpotReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotReservationRequestsEntity handle(PartRequestOfParkingSpotReserved partOfParkingSpotReserved) {
        reservations.add(new VehicleReservationEntity(
                partOfParkingSpotReserved.getReservationId().getValue(),
                partOfParkingSpotReserved.getVehicleSize().getValue()));
        return this;
    }

    private ParkingSpotReservationRequestsEntity handle(WholeRequestParkingSpotReserved wholeParkingSpotReserved) {
        reservations.add(new VehicleReservationEntity(
                wholeParkingSpotReserved.getReservationId().getValue(),
                capacity));
        return this;
    }

    private ParkingSpotReservationRequestsEntity handle(ParkingSpotReservationRequestCancelled parkingSpotReservationCancelled) {
        reservations.removeIf(reservation -> reservation.reservationId.equals(parkingSpotReservationCancelled.getReservationId().getValue()));
        return this;
    }

    @AllArgsConstructor
    static class VehicleReservationEntity {

        @NonNull UUID reservationId;
        @NonNull int size;

    }

}
