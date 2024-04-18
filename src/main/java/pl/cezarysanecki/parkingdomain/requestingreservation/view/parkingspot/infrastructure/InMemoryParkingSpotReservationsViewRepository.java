package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.infrastructure;

import pl.cezarysanecki.parkingdomain.commons.view.ViewEventListener;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationsView;
import pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.model.ParkingSpotReservationsViews;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot.ParkingSpotCreated;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.ParkingSpotReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.PartRequestOfParkingSpotReserved;
import static pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent.WholeRequestParkingSpotReserved;

class InMemoryParkingSpotReservationsViewRepository implements ParkingSpotReservationsViews {

    private static final Map<ParkingSpotId, ParkingSpotReservationsViewEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public Set<ParkingSpotReservationsView> getAllParkingSpots() {
        return DATABASE.values().stream()
                .map(DomainModelMapper::map)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<ParkingSpotReservationsView> getAvailableParkingSpots() {
        return DATABASE.values().stream()
                .filter(entity -> entity.currentOccupation() < entity.capacity)
                .map(DomainModelMapper::map)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotCreated event) {
        DATABASE.put(event.getParkingSpotId(), new ParkingSpotReservationsViewEntity(
                event.getParkingSpotId().getValue(),
                event.getParkingSpotCapacity().getValue(),
                new HashSet<>()));
    }

    @Override
    @ViewEventListener
    public void handle(WholeRequestParkingSpotReserved event) {
        ParkingSpotReservationsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentReservations.add(new ParkingSpotReservationsViewEntity.VehicleReservationEntity(
                event.getReservationId().getValue(),
                entity.capacity));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

    @Override
    @ViewEventListener
    public void handle(PartRequestOfParkingSpotReserved event) {
        ParkingSpotReservationsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentReservations.add(new ParkingSpotReservationsViewEntity.VehicleReservationEntity(
                event.getReservationId().getValue(),
                event.getVehicleSize().getValue()));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

    @Override
    @ViewEventListener
    public void handle(ParkingSpotReservationRequestCancelled event) {
        ParkingSpotReservationsViewEntity entity = DATABASE.get(event.getParkingSpotId());
        entity.currentReservations.removeIf(vehicleReservationEntity -> vehicleReservationEntity.reservationId.equals(event.getReservationId().getValue()));
        DATABASE.put(event.getParkingSpotId(), entity);
    }

}
