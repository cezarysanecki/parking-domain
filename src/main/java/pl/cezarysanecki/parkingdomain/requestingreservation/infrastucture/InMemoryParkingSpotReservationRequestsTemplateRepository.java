package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsTemplateRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotReservationRequestsTemplateRepository implements
        ParkingSpotReservationRequestsTemplateRepository {

    private static final Map<ParkingSpotId, ParkingSpotReservationRequestsTemplate> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void store(ParkingSpotReservationRequestsTemplate template) {
        DATABASE.put(template.parkingSpotId(), template);
    }

    @Override
    public List<ParkingSpotReservationRequestsTemplate> findAll() {
        return List.ofAll(DATABASE.values());
    }

}
