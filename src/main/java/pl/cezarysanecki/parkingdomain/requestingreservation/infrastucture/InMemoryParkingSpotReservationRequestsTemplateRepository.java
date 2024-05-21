package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ParkingSpotReservationRequestsTemplate;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ParkingSpotReservationRequestsTemplateRepository;

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

    static ParkingSpotReservationRequestsTemplate findBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .getOrElseThrow(() -> new IllegalStateException("cannot find template by parking spot id: " + parkingSpotId));
    }

}
