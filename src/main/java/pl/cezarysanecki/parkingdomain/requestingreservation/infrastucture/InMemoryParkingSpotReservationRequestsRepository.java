package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Predicate.not;
import static pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository.ParkingSpotReservationRequestsView.CapacityView;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotReservationRequestsRepository implements
        ParkingSpotReservationRequestsRepository,
        ParkingSpotReservationRequestsViewRepository {

    private static final Map<ParkingSpotTimeSlotId, ParkingSpotReservationRequests> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void save(ParkingSpotReservationRequests parkingSpotReservationRequests) {
        DATABASE.put(ParkingSpotTimeSlotId.newOne(), parkingSpotReservationRequests);
    }

    @Override
    public Option<ParkingSpotReservationRequests> findBy(ParkingSpotTimeSlotId parkingSpotTimeSlotId) {
        return Option.of(DATABASE.get(parkingSpotTimeSlotId));
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
    public io.vavr.collection.List<ParkingSpotReservationRequests> findAllWithRequests() {
        return io.vavr.collection.List.ofAll(
                DATABASE.values()
                        .stream()
                        .filter(not(ParkingSpotReservationRequests::isFree)));
    }

    @Override
    public List<ParkingSpotReservationRequestsView> queryForAllAvailableParkingSpots() {
        Map<ParkingSpotId, List<CapacityView>> result = new HashMap<>();
        DATABASE.forEach((key, value) -> {
            ParkingSpotId parkingSpotId = value.getParkingSpotId();
            List<CapacityView> currentCapacities = result.getOrDefault(parkingSpotId, new ArrayList<>());
            currentCapacities.add(new CapacityView(
                    key.getValue(),
                    value.getTimeSlot(),
                    value.getCapacity().getValue(),
                    value.spaceLeft()
            ));
            result.put(parkingSpotId, currentCapacities);
        });
        return result.entrySet()
                .stream()
                .map(entry -> new ParkingSpotReservationRequestsView(
                        entry.getKey().getValue(),
                        entry.getValue()
                ))
                .toList();
    }

}
