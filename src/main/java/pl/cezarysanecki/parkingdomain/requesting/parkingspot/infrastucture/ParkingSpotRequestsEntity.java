package pl.cezarysanecki.parkingdomain.requesting.parkingspot.infrastucture;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancelled;
import pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored;

import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForWholeParkingSpotStored;

@AllArgsConstructor
class ParkingSpotRequestsEntity {

    UUID parkingSpotId;
    int capacity;
    Set<VehicleRequestEntity> requests;

    ParkingSpotRequestsEntity handle(ParkingSpotRequestEvent domainEvent) {
        return Match(domainEvent).of(
                Case($(instanceOf(RequestForPartOfParkingSpotStored.class)), this::handle),
                Case($(instanceOf(RequestForWholeParkingSpotStored.class)), this::handle),
                Case($(instanceOf(ParkingSpotRequestCancelled.class)), this::handle),
                Case($(), () -> this));
    }

    private ParkingSpotRequestsEntity handle(RequestForPartOfParkingSpotStored requestForPartOfParkingSpotStored) {
        requests.add(new VehicleRequestEntity(
                requestForPartOfParkingSpotStored.getRequestId().getValue(),
                requestForPartOfParkingSpotStored.getSpotUnits().getValue()));
        return this;
    }

    private ParkingSpotRequestsEntity handle(RequestForWholeParkingSpotStored requestForWholeParkingSpotStored) {
        requests.add(new VehicleRequestEntity(
                requestForWholeParkingSpotStored.getRequestId().getValue(),
                capacity));
        return this;
    }

    private ParkingSpotRequestsEntity handle(ParkingSpotRequestCancelled parkingSpotRequestCancelled) {
        requests.removeIf(request -> request.requestId.equals(parkingSpotRequestCancelled.getRequestId().getValue()));
        return this;
    }

    @AllArgsConstructor
    static class VehicleRequestEntity {

        @NonNull UUID requestId;
        int size;

    }

}
