package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryReservationScheduleRepository implements ParkingSpotReservationsRepository {

    private static final Map<ParkingSpotId, ParkingReservationsEntity> DATABASE = new ConcurrentHashMap<>();
    private final EventPublisher eventPublisher;

    @Override
    public void createFor(ParkingSpotId parkingSpotId, ParkingSpotType parkingSpotType, int capacity) {
        DATABASE.put(parkingSpotId, new ParkingReservationsEntity(
                parkingSpotId.getValue(),
                parkingSpotType,
                capacity));
    }

    @Override
    public Option<ParkingSpotReservations> findBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .map(DomainModelMapper::map);
    }

    @Override
    public Option<ParkingSpotReservations> findFor(ParkingSpotType parkingSpotType, VehicleSizeUnit vehicleSizeUnit) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(entity -> entity.parkingSpotType == parkingSpotType)
                        .filter(entity -> entity.capacity >= vehicleSizeUnit.getValue())
                        .findFirst()
                        .map(DomainModelMapper::map));
    }

    @Override
    public Option<ParkingSpotReservations> findBy(ReservationId reservationId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(parkingReservationsEntity -> parkingReservationsEntity.collection.stream()
                                .anyMatch(reservationEntity -> reservationEntity.reservationId.equals(reservationId.getValue())))
                        .findFirst()
                        .map(DomainModelMapper::map));
    }

    @Override
    public ParkingSpotReservations publish(ParkingSpotReservationsEvent event) {
        ParkingSpotReservations parkingSpotReservations = handle(event);
        eventPublisher.publish(event.normalize());
        return parkingSpotReservations;
    }

    private ParkingSpotReservations handle(ParkingSpotReservationsEvent event) {
        ParkingReservationsEntity entity = DATABASE.get(event.getParkingSpotId());
        entity = entity.handle(event);
        DATABASE.put(event.getParkingSpotId(), entity);
        return DomainModelMapper.map(entity);
    }

}
