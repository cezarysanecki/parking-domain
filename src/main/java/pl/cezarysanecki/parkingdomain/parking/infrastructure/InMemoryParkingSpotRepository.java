package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.web.ParkingSpotViewRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Predicate.not;

class InMemoryParkingSpotRepository implements
        ParkingSpotRepository,
        ParkingSpotViewRepository {

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
    public Option<ParkingSpot> findBy(OccupationId occupationId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(parkingSpot -> parkingSpot.getOccupations().containsKey(occupationId))
                        .findFirst());
    }

    @Override
    public Option<ParkingSpot> findBy(ReservationId reservationId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(parkingSpot -> parkingSpot.getReservations().containsKey(reservationId))
                        .findFirst());
    }

    @Override
    public List<CapacityView> queryForAllAvailableParkingSpots() {
        return DATABASE.values()
                .stream()
                .filter(not(ParkingSpot::isFull))
                .map(parkingSpot -> new CapacityView(
                        parkingSpot.getParkingSpotId().getValue(),
                        parkingSpot.getCapacity().getValue(),
                        parkingSpot.spaceLeft()
                ))
                .toList();
    }
}
