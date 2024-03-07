package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryParkingSpotRepository implements ParkingSpots {

    private final Map<ParkingSpotId, ParkingSpot> database = new ConcurrentHashMap<>();

    @Override
    public Optional<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
        return Optional.ofNullable(database.get(parkingSpotId));
    }

    @Override
    public void save(ParkingSpot parkingSpot) {
        database.put(parkingSpot.getParkingSpotId(), parkingSpot);
    }

}
