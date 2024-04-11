package pl.cezarysanecki.parkingdomain.reserving.client.infrastucture;

import io.vavr.API;
import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForPartOfParkingSpotSubmitted;
import pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationRequestCancelled;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.reserving.client.model.ClientReservationsEvent.ReservationForWholeParkingSpotSubmitted;

@AllArgsConstructor
class ClientReservationsEntity {

    UUID clientId;
    Set<UUID> reservations;

    ClientReservationsEntity handle(ClientReservationsEvent domainEvent) {
        return Match(domainEvent).of(
                API.Case(API.$(instanceOf(ReservationForWholeParkingSpotSubmitted.class)), this::handle),
                API.Case(API.$(instanceOf(ReservationForPartOfParkingSpotSubmitted.class)), this::handle),
                API.Case(API.$(instanceOf(ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientReservationsEntity handle(ReservationForWholeParkingSpotSubmitted reservationForWholeParkingSpotSubmitted) {
        reservations.add(reservationForWholeParkingSpotSubmitted.getReservationId().getValue());
        return this;
    }

    private ClientReservationsEntity handle(ReservationForPartOfParkingSpotSubmitted reservationForPartOfParkingSpotSubmitted) {
        reservations.add(reservationForPartOfParkingSpotSubmitted.getReservationId().getValue());
        return this;
    }

    private ClientReservationsEntity handle(ReservationRequestCancelled reservationRequestCancelled) {
        reservations.removeIf(reservation -> reservation.equals(reservationRequestCancelled.getReservationId().getValue()));
        return this;
    }

}
