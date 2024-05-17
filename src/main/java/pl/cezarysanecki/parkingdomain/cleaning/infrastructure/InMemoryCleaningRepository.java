package pl.cezarysanecki.parkingdomain.cleaning.infrastructure;

import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningRepository;
import pl.cezarysanecki.parkingdomain.cleaning.model.CurrentCounterValue;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryCleaningRepository implements CleaningRepository {

    private final Map<ParkingSpotId, Integer> DATABASE = new ConcurrentHashMap<>();

    @Override
    public CurrentCounterValue increaseCounterFor(ParkingSpotId parkingSpotId) {
        Integer counter = DATABASE.getOrDefault(parkingSpotId, 0);
        DATABASE.put(parkingSpotId, ++counter);
        return new CurrentCounterValue(counter);
    }

    @Override
    public void resetCounter(ParkingSpotId parkingSpotId) {
        DATABASE.put(parkingSpotId, 0);
    }

}
