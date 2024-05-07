package pl.cezarysanecki.parkingdomain.parking;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.web.ParkingSpotViewRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

interface ParkingSpotRepository {

    void save(ParkingSpot parkingSpot);

    Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId);

    class InMemoryParkingSpotRepository implements ParkingSpotRepository, ParkingSpotViewRepository {

        static final Map<ParkingSpotId, ParkingSpot> DATABASE = new ConcurrentHashMap<>();

        @Override
        public void save(ParkingSpot parkingSpot) {
            DATABASE.put(parkingSpot.getParkingSpotId(), parkingSpot);
        }

        @Override
        public Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
            return Option.of(DATABASE.get(parkingSpotId));
        }

        @Override
        public List<CapacityView> queryForAllAvailableParkingSpots() {
            return DATABASE.values()
                    .stream()
                    .filter(Predicate.not(ParkingSpot::isFull))
                    .map(parkingSpot -> new CapacityView(
                            parkingSpot.getParkingSpotId().getValue(),
                            parkingSpot.getCapacity().getValue(),
                            parkingSpot.spaceLeft()
                    ))
                    .toList();
        }
    }

}
