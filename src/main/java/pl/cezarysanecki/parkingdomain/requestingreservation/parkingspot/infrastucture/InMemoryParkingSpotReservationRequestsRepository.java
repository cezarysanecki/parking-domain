package pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.infrastucture;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.application.FindingParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.parkingspot.model.ParkingSpotReservationRequestsRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotReservationRequestsRepository implements ParkingSpotReservationRequestsRepository, FindingParkingSpotReservationRequests {

    private static final Map<ParkingSpotId, ParkingSpotReservationRequestsEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public Option<ParkingSpotId> findParkingSpotIdBy(ReservationId reservationId) {
        return findBy(reservationId)
                .map(ParkingSpotReservationRequests::getParkingSpotId);
    }

    @Override
    public ParkingSpotReservationRequests createUsing(ParkingSpotId parkingSpotId, ParkingSpotCapacity parkingSpotCapacity) {
        ParkingSpotReservationRequestsEntity entity = new ParkingSpotReservationRequestsEntity(
                parkingSpotId.getValue(),
                parkingSpotCapacity.getValue(),
                new HashSet<>());
        DATABASE.put(parkingSpotId, entity);
        return DomainModelMapper.map(entity);
    }

    @Override
    public Option<ParkingSpotReservationRequests> findBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .map(DomainModelMapper::map);
    }

    @Override
    public Option<ParkingSpotReservationRequests> findBy(ReservationId reservationId) {
        return Option.ofOptional(
                        DATABASE.values()
                                .stream()
                                .filter(entity -> entity.reservationRequests.stream()
                                        .anyMatch(vehicleReservationEntity -> vehicleReservationEntity.reservationId.equals(reservationId.getValue())))
                                .findFirst())
                .map(DomainModelMapper::map);
    }

    @Override
    public void publish(ParkingSpotReservationRequestEvent parkingSpotReservationRequestEvent) {
        ParkingSpotId parkingSpotId = parkingSpotReservationRequestEvent.getParkingSpotId();

        ParkingSpotReservationRequestsEntity entity = DATABASE.get(parkingSpotId);
        if (entity != null) {
            entity.handle(parkingSpotReservationRequestEvent);
            DATABASE.put(parkingSpotId, entity);
        } else {
            log.debug("cannot find parking spot reservation for parking spot with id {}", parkingSpotId);
        }
        eventPublisher.publish(parkingSpotReservationRequestEvent.normalize());
    }
}
