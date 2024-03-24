package pl.cezarysanecki.parkingdomain.client.reservationrequest.infrastructure;

import io.vavr.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.AnyParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ChosenParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.reservationrequest.model.ClientReservationRequestsEvent.ReservationRequestCancelled;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ClientReservationsEntity {

    @Id
    Long id;
    UUID clientId;
    Set<UUID> clientReservations;

    ClientReservationsEntity(UUID clientId) {
        this.clientId = clientId;
        this.clientReservations = new HashSet<>();
    }

    void handle(ClientReservationRequestsEvent event) {
        API.Match(event).of(
                API.Case(API.$(instanceOf(ChosenParkingSpotReservationRequested.class)), this::handle),
                API.Case(API.$(instanceOf(AnyParkingSpotReservationRequested.class)), this::handle),
                API.Case(API.$(instanceOf(ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientReservationsEntity handle(ChosenParkingSpotReservationRequested chosenParkingSpotReservationRequested) {
        this.clientReservations.add(chosenParkingSpotReservationRequested.getReservationId().getValue());
        return this;
    }

    private ClientReservationsEntity handle(AnyParkingSpotReservationRequested anyParkingSpotReservationRequested) {
        this.clientReservations.add(anyParkingSpotReservationRequested.getReservationId().getValue());
        return this;
    }

    private ClientReservationsEntity handle(ReservationRequestCancelled reservationRequestCancelled) {
        this.clientReservations.remove(reservationRequestCancelled.getReservationId().getValue());
        return this;
    }

}
