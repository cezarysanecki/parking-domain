package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.occupation.OccupationId;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.parkingspot.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.parking.model.reservation.ReservationId;
import pl.cezarysanecki.parkingdomain.parking.web.ParkingSpotViewRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Predicate.not;

class InMemoryParkingSpotRepository implements
    ParkingSpotRepository,
    ParkingSpotViewRepository {

  static final Map<ParkingSpotId, ParkingSpot> DATABASE = new ConcurrentHashMap<>();

  @Override
  public void saveNew(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity, ParkingSpotCategory category) {

  }

  @Override
  public Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
    return Option.of(DATABASE.get(parkingSpotId));
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
  public void publish(ParkingSpotEvent event) {

  }

  @Override
  public Option<ParkingSpot> findAvailableBy(ParkingSpotCategory category) {
    return Option.ofOptional(
        DATABASE.values()
            .stream()
            .filter(parkingSpot -> parkingSpot.getCategory() == category)
            .filter(not(ParkingSpot::isFull))
            .findFirst());
  }

  @Override
  public List<CapacityView> queryForAllAvailableParkingSpots() {
    return DATABASE.values()
        .stream()
        .filter(not(ParkingSpot::isFull))
        .map(parkingSpot -> new CapacityView(
            parkingSpot.getParkingSpotId().getValue(),
            parkingSpot.getCategory(),
            parkingSpot.getCapacity().getValue(),
            parkingSpot.spaceLeft()
        ))
        .toList();
  }

}
