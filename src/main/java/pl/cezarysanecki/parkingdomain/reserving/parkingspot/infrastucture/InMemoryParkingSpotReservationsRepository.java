package pl.cezarysanecki.parkingdomain.reserving.parkingspot.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationEvent;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.reserving.parkingspot.model.ParkingSpotReservationsRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotReservationsRepository implements ParkingSpotReservationsRepository {

    private static final Map<ParkingSpotId, ParkingSpotReservationsEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public ParkingSpotReservations createUsing(ParkingSpotId parkingSpotId, ParkingSpotCapacity parkingSpotCapacity) {
        ParkingSpotReservationsEntity entity = new ParkingSpotReservationsEntity(
                parkingSpotId.getValue(),
                parkingSpotCapacity.getValue(),
                new HashSet<>());
        DATABASE.put(parkingSpotId, entity);
        return DomainModelMapper.map(entity);
    }

    @Override
    public Option<ParkingSpotReservations> findBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .map(DomainModelMapper::map);
    }

    @Override
    public void publish(ParkingSpotReservationEvent parkingSpotReservationEvent) {
        ParkingSpotId parkingSpotId = parkingSpotReservationEvent.getParkingSpotId();

        ParkingSpotReservationsEntity entity = DATABASE.get(parkingSpotId);
        if (entity != null) {
            entity.handle(parkingSpotReservationEvent);
            DATABASE.put(parkingSpotId, entity);
        } else {
            log.debug("cannot find parking spot reservation for parking spot with id {}", parkingSpotId);
        }
        eventPublisher.publish(parkingSpotReservationEvent.normalize());
    }

}
