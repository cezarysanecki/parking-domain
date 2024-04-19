package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.infrastructure;

import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationRequestsView;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationRequestsViews;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForPartOfParkingSpotStored;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ReservationRequestForWholeParkingSpotStored;

class InMemoryParkingSpotReservationRequestsViewRepository implements ParkingSpotReservationRequestsViews {

    private static final Map<ParkingSpotId, ParkingSpotReservationRequestsViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public Set<ParkingSpotReservationRequestsView> getAllParkingSpots() {
        return DATABASE.values().stream()
                .map(DomainModelMapper::map)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<ParkingSpotReservationRequestsView> getAvailableParkingSpots() {
        return DATABASE.values().stream()
                .filter(entity -> entity.currentOccupation() < entity.capacity)
                .map(DomainModelMapper::map)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotCreated event) {
        DATABASE.put(event.getParkingSpotId(), new ParkingSpotReservationRequestsViewEntity(
                event.getParkingSpotId().getValue(),
                event.getParkingSpotCapacity().getValue(),
                new HashSet<>()));
    }

    @Override
    @ViewEventListener
    public void handle(ReservationRequestForWholeParkingSpotStored event) {
        ParkingSpotReservationRequestsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentReservations.add(new ParkingSpotReservationRequestsViewEntity.VehicleReservationRequestEntity(
                event.getReservationId().getValue(),
                entity.capacity));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

    @Override
    @ViewEventListener
    public void handle(ReservationRequestForPartOfParkingSpotStored event) {
        ParkingSpotReservationRequestsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentReservations.add(new ParkingSpotReservationRequestsViewEntity.VehicleReservationRequestEntity(
                event.getReservationId().getValue(),
                event.getVehicleSize().getValue()));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotReservationRequestCancelled event) {
        ParkingSpotReservationRequestsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentReservations.removeIf(vehicleReservationRequestEntity -> vehicleReservationRequestEntity.reservationId.equals(event.getReservationId().getValue()));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

}
