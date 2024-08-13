package pl.cezarysanecki.parkingdomain.cleaning;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryCleaningRepository implements CleaningRepository {

  private final Map<ParkingSpotId, Integer> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void increaseCounterFor(ParkingSpotId parkingSpotId) {
    Integer counter = DATABASE.getOrDefault(parkingSpotId, 0);
    DATABASE.put(parkingSpotId, ++counter);
  }

  @Override
  public void resetAll() {
    DATABASE.clear();
  }

  @Override
  public List<ParkingSpotId> getAllRecordsWithCounterAbove(int limit) {
    return DATABASE.entrySet()
        .stream()
        .filter(entry -> entry.getValue() >= limit)
        .map(Map.Entry::getKey)
        .toList();
  }

}
