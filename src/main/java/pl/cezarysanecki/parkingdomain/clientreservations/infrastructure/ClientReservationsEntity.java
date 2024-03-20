package pl.cezarysanecki.parkingdomain.clientreservations.infrastructure;

import io.vavr.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCancelled;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientReservationsEvent.ReservationRequestCreated;

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
                Case($(instanceOf(ReservationRequestCreated.class)), this::handle),
                Case($(instanceOf(ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientReservationsEntity handle(ReservationRequestCreated reservationRequestCreated) {
        this.reservations.add(reservationRequestCreated.getReservationId().getValue());
        return this;
    }

    private ClientReservationsEntity handle(ReservationRequestCancelled reservationRequestCancelled) {
        this.reservations.remove(reservationRequestCancelled.getReservationId().getValue());
        return this;
    }

}
