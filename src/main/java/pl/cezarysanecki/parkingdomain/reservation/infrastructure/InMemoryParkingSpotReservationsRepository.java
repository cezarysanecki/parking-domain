package pl.cezarysanecki.parkingdomain.reservation.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotType;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.reservation.model.events.ParkingSpotReservationsEvent;
import pl.cezarysanecki.parkingdomain.reservation.model.ParkingSpotReservationsRepository;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotReservationsRepository implements ParkingSpotReservationsRepository {

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
        return Option.ofOptional(
                        DATABASE.values()
                                .stream()
                                .filter(entity -> entity.collection.stream()
                                        .anyMatch(reservationEntity -> reservationEntity.reservationId.equals(event.getReservationId().getValue())))
                                .findFirst())
                .map(entity -> {
                    entity = entity.handle(event);
                    DATABASE.put(ParkingSpotId.of(entity.parkingSpotId), entity);
                    return DomainModelMapper.map(entity);
                })
                .getOrElseThrow(() -> new IllegalStateException("there should be parking spot reservations containing reservation with id " + event.getReservationId()));
    }

}
