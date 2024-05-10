package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Predicate.not;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotReservationRequestsRepository implements
        ParkingSpotReservationRequestsRepository,
        ParkingSpotReservationRequestsViewRepository {

    private static final Map<ParkingSpotId, ParkingSpotReservationRequests> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void save(ParkingSpotReservationRequests parkingSpotReservationRequests) {
        DATABASE.put(parkingSpotReservationRequests.getParkingSpotId(), parkingSpotReservationRequests);
    }

    @Override
    public Option<ParkingSpotReservationRequests> findBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId));
    }

    @Override
    public Option<ParkingSpotReservationRequests> findBy(ReservationRequestId reservationRequestId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(reservationRequests -> reservationRequests.getReservationRequests().containsKey(reservationRequestId))
                        .findFirst());
    }

    @Override
    public List<ParkingSpotReservationRequests> findAllWithRequests() {
        return List.ofAll(
                DATABASE.values()
                        .stream()
                        .filter(not(ParkingSpotReservationRequests::isFree)));
    }

    @Override
    public List<CapacityView> queryForAllAvailableParkingSpots() {
        return List.ofAll(
                DATABASE.values()
                        .stream()
                        .map(reservationRequests -> new ParkingSpotReservationRequestsViewRepository.CapacityView(
                                reservationRequests.getParkingSpotId().getValue(),
                                reservationRequests.getCapacity().getValue(),
                                reservationRequests.spaceLeft()
                        )));
    }

}
