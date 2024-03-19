package pl.cezarysanecki.parkingdomain.parkingview.infrastructure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parkingview.model.AvailableParkingSpotView;
import pl.cezarysanecki.parkingdomain.parkingview.model.AvailableParkingSpotsView;
import pl.cezarysanecki.parkingdomain.parkingview.model.ParkingViews;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class InMemoryParkingViewReadModel implements ParkingViews {

    private static final Map<ParkingSpotId, ParkingSpotViewEntityModel> DATABASE = new ConcurrentHashMap<>();

    @Override
    public AvailableParkingSpotsView findAvailable() {
        return new AvailableParkingSpotsView(DATABASE.values()
                .stream()
                .map(entity -> new AvailableParkingSpotView(
                        ParkingSpotId.of(entity.parkingSpotId),
                        entity.leftCapacity))
                .collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public void addParkingSpot(ParkingSpotId parkingSpotId, int capacity) {
        DATABASE.put(parkingSpotId, new ParkingSpotViewEntityModel(parkingSpotId.getValue(), capacity));
        log.debug("creating parking spot view with id {}", parkingSpotId);
    }

    @Override
    public void increaseCapacity(ParkingSpotId parkingSpotId, int delta) {
        ParkingSpotViewEntityModel entity = DATABASE.getOrDefault(
                parkingSpotId, new ParkingSpotViewEntityModel(parkingSpotId.getValue(), 0));
        entity.leftCapacity += delta;

        DATABASE.put(parkingSpotId, entity);
        log.debug("increasing parking spot view capacity with id {}", parkingSpotId);
    }

    @Override
    public void decreaseCapacity(ParkingSpotId parkingSpotId, int delta) {
        ParkingSpotViewEntityModel entity = DATABASE.getOrDefault(
                parkingSpotId, new ParkingSpotViewEntityModel(parkingSpotId.getValue(), 0));
        entity.leftCapacity -= delta;
        if (entity.leftCapacity > 0) {
            DATABASE.put(parkingSpotId, entity);
            log.debug("decreasing parking spot view capacity with id {}", parkingSpotId);
        } else {
            removeParkingSpot(parkingSpotId);
        }
    }

    @Override
    public void removeParkingSpot(ParkingSpotId parkingSpotId) {
        DATABASE.values().removeIf(entity -> entity.parkingSpotId.equals(parkingSpotId.getValue()));
        log.debug("removing parking spot view with id {}", parkingSpotId);
    }

    @Data
    @AllArgsConstructor
    private static class ParkingSpotViewEntityModel {

        @NonNull
        UUID parkingSpotId;
        int leftCapacity;

    }


}

