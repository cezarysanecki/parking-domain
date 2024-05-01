package pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.infrastructure;

import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsView;
import pl.cezarysanecki.parkingdomain.requesting.view.parkingspot.model.ParkingSpotRequestsViews;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.ParkingSpotRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForPartOfParkingSpotStored;
import static pl.cezarysanecki.parkingdomain.requesting.parkingspot.model.ParkingSpotRequestEvent.RequestForWholeParkingSpotStored;

class InMemoryParkingSpotRequestsViewRepository implements ParkingSpotRequestsViews {

    private static final Map<ParkingSpotId, ParkingSpotRequestsViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public Set<ParkingSpotRequestsView> getAllParkingSpots() {
        return DATABASE.values().stream()
                .map(DomainModelMapper::map)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<ParkingSpotRequestsView> getAvailableParkingSpots() {
        return DATABASE.values().stream()
                .filter(entity -> entity.currentOccupation() < entity.capacity)
                .map(DomainModelMapper::map)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotAdded event) {
        DATABASE.put(event.parkingSpotId(), new ParkingSpotRequestsViewEntity(
                event.parkingSpotId().getValue(),
                event.capacity().getValue(),
                new HashSet<>()));
    }

    @Override
    @ViewEventListener
    public void handle(RequestForWholeParkingSpotStored event) {
        ParkingSpotRequestsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentRequests.add(new ParkingSpotRequestsViewEntity.VehicleRequestEntity(
                event.getRequestId().getValue(),
                entity.capacity));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

    @Override
    @ViewEventListener
    public void handle(RequestForPartOfParkingSpotStored event) {
        ParkingSpotRequestsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentRequests.add(new ParkingSpotRequestsViewEntity.VehicleRequestEntity(
                event.getRequestId().getValue(),
                event.getSpotUnits().getValue()));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotRequestCancelled event) {
        ParkingSpotRequestsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentRequests.removeIf(vehicleRequestEntity -> vehicleRequestEntity.requestId.equals(event.getRequestId().getValue()));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

}
