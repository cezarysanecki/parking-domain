package pl.cezarysanecki.parkingdomain.reservation.client.infrastucture;

import io.vavr.API;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.client.model.ClientReservationsEvent.ReservationRequestSubmitted;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

class ClientReservationsEntity {

    UUID clientId;
    Set<UUID> reservations;

    ClientReservationsEntity handle(ClientReservationsEvent domainEvent) {
        return Match(domainEvent).of(
                API.Case(API.$(instanceOf(ReservationRequestSubmitted.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientReservationsEntity handle(ReservationRequestSubmitted reservationRequestSubmitted) {
        reservations.add(reservationRequestSubmitted.getReservationRequest().getReservationId().getValue());
        return this;
    }

}
