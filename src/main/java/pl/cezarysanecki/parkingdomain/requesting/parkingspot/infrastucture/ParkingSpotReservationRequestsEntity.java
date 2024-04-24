package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForPartOfParkingSpotStored;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForWholeParkingSpotStored;

@AllArgsConstructor
class ParkingSpotReservationRequestsEntity {

    UUID parkingSpotId;
    int capacity;
    Set<VehicleReservationRequestEntity> reservationRequests;

    ParkingSpotReservationRequestsEntity handle(ParkingSpotReservationRequestEvent domainEvent) {
        return Match(domainEvent).of(
                Case($(instanceOf(ReservationRequestForPartOfParkingSpotStored.class)), this::handle),
                Case($(instanceOf(ReservationRequestForWholeParkingSpotStored.class)), this::handle),
                Case($(instanceOf(ParkingSpotReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotReservationRequestsEntity handle(ReservationRequestForPartOfParkingSpotStored partOfParkingSpotReserved) {
        reservationRequests.add(new VehicleReservationRequestEntity(
                partOfParkingSpotReserved.getReservationId().getValue(),
                partOfParkingSpotReserved.getVehicleSize().getValue()));
        return this;
    }

    private ParkingSpotReservationRequestsEntity handle(ReservationRequestForWholeParkingSpotStored wholeParkingSpotReserved) {
        reservationRequests.add(new VehicleReservationRequestEntity(
                wholeParkingSpotReserved.getReservationId().getValue(),
                capacity));
        return this;
    }

    private ParkingSpotReservationRequestsEntity handle(ParkingSpotReservationRequestCancelled parkingSpotReservationCancelled) {
        reservationRequests.removeIf(reservation -> reservation.reservationId.equals(parkingSpotReservationCancelled.getReservationId().getValue()));
        return this;
    }

    @AllArgsConstructor
    static class VehicleReservationRequestEntity {

        @NonNull UUID reservationId;
        @NonNull int size;

    }

}
