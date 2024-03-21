package pl.cezarysanecki.parkingdomain.views.parking.infrastructure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.views.parking.model.AvailableParkingSpotView;
import pl.cezarysanecki.parkingdomain.views.parking.model.AvailableParkingSpotsView;
import pl.cezarysanecki.parkingdomain.views.parking.model.ParkingViews;

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
                .filter(entity -> entity.leftCapacity > 0)
                .map(entity -> new AvailableParkingSpotView(
                        ParkingSpotId.of(entity.parkingSpotId),
                        entity.parkingSpotType,
                        entity.leftCapacity))
                .collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public void addParkingSpot(ParkingSpotId parkingSpotId, ParkingSpotType parkingSpotType, int capacity) {
        DATABASE.put(parkingSpotId, new ParkingSpotViewEntityModel(parkingSpotId.getValue(), parkingSpotType, capacity));
        log.debug("creating parking spot view with id {}", parkingSpotId);
    }

    @Override
    public void increaseCapacity(ParkingSpotId parkingSpotId, int delta) {
        ParkingSpotViewEntityModel entity = DATABASE.get(parkingSpotId);
        entity.leftCapacity += delta;

        DATABASE.put(parkingSpotId, entity);
        log.debug("increasing parking spot view capacity with id {}", parkingSpotId);
    }

    @Override
    public void decreaseCapacity(ParkingSpotId parkingSpotId, int delta) {
        ParkingSpotViewEntityModel entity = DATABASE.get(parkingSpotId);
        entity.leftCapacity -= delta;

        DATABASE.put(parkingSpotId, entity);
        log.debug("decreasing parking spot view capacity with id {}", parkingSpotId);
    }

    @Data
    @AllArgsConstructor
    private static class ParkingSpotViewEntityModel {

        @NonNull
        UUID parkingSpotId;
        ParkingSpotType parkingSpotType;
        int leftCapacity;

    }


}

