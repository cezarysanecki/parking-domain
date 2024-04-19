package pl.cezarysanecki.parkingdomain.requestingreservation.client.infrastucture;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForPartOfParkingSpotRequested;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationRequestCancelled;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ClientReservationRequestsEvent.ReservationForWholeParkingSpotRequested;

@AllArgsConstructor
class ClientReservationRequestsEntity {

    UUID clientId;
    Set<UUID> reservations;

    ClientReservationRequestsEntity handle(ClientReservationRequestsEvent domainEvent) {
        return Match(domainEvent).of(
                Case($(instanceOf(ReservationForWholeParkingSpotRequested.class)), this::handle),
                Case($(instanceOf(ReservationForPartOfParkingSpotRequested.class)), this::handle),
                Case($(instanceOf(ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientReservationRequestsEntity handle(ReservationForWholeParkingSpotRequested reservationForWholeParkingSpotRequested) {
        reservations.add(reservationForWholeParkingSpotRequested.getReservationId().getValue());
        return this;
    }

    private ClientReservationRequestsEntity handle(ReservationForPartOfParkingSpotRequested reservationForPartOfParkingSpotRequested) {
        reservations.add(reservationForPartOfParkingSpotRequested.getReservationId().getValue());
        return this;
    }

    private ClientReservationRequestsEntity handle(ReservationRequestCancelled reservationRequestCancelled) {
        reservations.removeIf(reservation -> reservation.equals(reservationRequestCancelled.getReservationId().getValue()));
        return this;
    }

}
