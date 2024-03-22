package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import io.vavr.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationsEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ClientReservationsEntity {

    @Id
    Long id;
    UUID clientId;
    Set<UUID> reservations;

    ClientReservationsEntity(UUID clientId) {
        this.clientId = clientId;
        this.reservations = new HashSet<>();
    }

    void handle(ClientReservationsEvent event) {
        API.Match(event).of(
                API.Case(API.$(instanceOf(ClientReservationsEvent.ReservationRequestCreated.class)), this::handle),
                API.Case(API.$(instanceOf(ClientReservationsEvent.ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientReservationsEntity handle(ClientReservationsEvent.ReservationRequestCreated reservationRequestCreated) {
        this.reservations.add(reservationRequestCreated.getReservationId().getValue());
        return this;
    }

    private ClientReservationsEntity handle(ClientReservationsEvent.ReservationRequestCancelled reservationRequestCancelled) {
        this.reservations.remove(reservationRequestCancelled.getReservationId().getValue());
        return this;
    }

}