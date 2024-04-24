package pl.cezarysanecki.parkingdomain.requesting.client.infrastucture;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestCancelled;
import pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForPartOfParkingSpotMade;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.requesting.client.model.ClientRequestsEvent.RequestForWholeParkingSpotMade;

@AllArgsConstructor
class ClientRequestsEntity {

    UUID clientId;
    Set<UUID> requests;

    ClientRequestsEntity handle(ClientRequestsEvent domainEvent) {
        return Match(domainEvent).of(
                Case($(instanceOf(RequestForWholeParkingSpotMade.class)), this::handle),
                Case($(instanceOf(RequestForPartOfParkingSpotMade.class)), this::handle),
                Case($(instanceOf(RequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ClientRequestsEntity handle(RequestForWholeParkingSpotMade requestForWholeParkingSpotMade) {
        requests.add(requestForWholeParkingSpotMade.getRequestId().getValue());
        return this;
    }

    private ClientRequestsEntity handle(RequestForPartOfParkingSpotMade requestForPartOfParkingSpotMade) {
        requests.add(requestForPartOfParkingSpotMade.getRequestId().getValue());
        return this;
    }

    private ClientRequestsEntity handle(RequestCancelled requestCancelled) {
        requests.removeIf(request -> request.equals(requestCancelled.getRequestId().getValue()));
        return this;
    }

}
