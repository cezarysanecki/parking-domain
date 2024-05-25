package pl.cezarysanecki.parkingdomain.cleaning.infrastructure;

import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.cleaning.model.CleaningRepository;
import pl.cezarysanecki.parkingdomain.cleaning.web.CleaningViewRepository;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
class InMemoryCleaningRepository implements CleaningRepository, CleaningViewRepository {

  private final Map<ParkingSpotId, Integer> DATABASE = new ConcurrentHashMap<>();

  private final int numberOfDrivesAwayToConsiderParkingSpotDirty;

  @Override
  public void increaseCounterFor(ParkingSpotId parkingSpotId) {
    Integer counter = DATABASE.getOrDefault(parkingSpotId, 0);
    DATABASE.put(parkingSpotId, ++counter);
  }

  @Override
  public void resetCountersFor(List<ParkingSpotId> parkingSpotIds) {
    parkingSpotIds.forEach(
        parkingSpotId -> DATABASE.put(parkingSpotId, 0)
    );
  }

  @Override
  public List<ParkingSpotId> getAllRecordsWithCounterAbove(int limit) {
    return DATABASE.entrySet()
        .stream()
        .filter(entry -> entry.getValue() >= limit)
        .map(Map.Entry::getKey)
        .toList();
  }

  @Override
  public CleaningView queryForCleaningView() {
    long exceedsOfThreshold = DATABASE.values()
        .stream()
        .filter(counter -> counter >= numberOfDrivesAwayToConsiderParkingSpotDirty)
        .count();
    List<CleaningView.ParkingSpot> parkingSpots = DATABASE.entrySet()
        .stream()
        .map(entry -> new CleaningView.ParkingSpot(entry.getKey().getValue(), entry.getValue()))
        .toList();
    return new CleaningView(exceedsOfThreshold, parkingSpots);
  }

}
