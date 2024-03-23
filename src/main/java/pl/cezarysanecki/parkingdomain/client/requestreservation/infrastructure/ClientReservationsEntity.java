package pl.cezarysanecki.parkingdomain.client.requestreservation.infrastructure;

import io.vavr.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.AnyParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ChosenParkingSpotReservationRequested;
import static pl.cezarysanecki.parkingdomain.client.requestreservation.model.ClientReservationRequestsEvent.ReservationRequestCancelled;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ClientReservationsEntity {

    @Id
    Long id;
    UUID clientId;
    Set<UUID> clientReservationRequests;

    ClientReservationsEntity(UUID clientId) {
        this.clientId = clientId;
        this.clientReservationRequests = new HashSet<>();
    }

    void handle(ClientReservationRequestsEvent event) {
        API.Match(event).of(
                API.Case(API.$(instanceOf(ChosenParkingSpotReservationRequested.class)), this::handle),
                API.Case(API.$(instanceOf(AnyParkingSpotReservationRequested.class)), this::handle),
                API.Case(API.$(instanceOf(ReservationRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientReservationsEntity handle(ChosenParkingSpotReservationRequested chosenParkingSpotReservationRequested) {
        this.clientReservationRequests.add(chosenParkingSpotReservationRequested.getClientReservationRequestId().getValue());
        return this;
    }

    private ClientReservationsEntity handle(AnyParkingSpotReservationRequested anyParkingSpotReservationRequested) {
        this.clientReservationRequests.add(anyParkingSpotReservationRequested.getClientReservationRequestId().getValue());
        return this;
    }

    private ClientReservationsEntity handle(ReservationRequestCancelled reservationRequestCancelled) {
        this.clientReservationRequests.remove(reservationRequestCancelled.getClientReservationRequestId().getValue());
        return this;
    }

}
