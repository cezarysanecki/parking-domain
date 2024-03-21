package pl.cezarysanecki.parkingdomain.reservation.schedule.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSchedule;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationScheduleEvent;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSchedules;
import pl.cezarysanecki.parkingdomain.reservation.schedule.model.ReservationSlot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
class InMemoryReservationScheduleRepository implements ReservationSchedules {

    private static final Map<ParkingSpotId, ReservationsEntity> DATABASE = new ConcurrentHashMap<>();
    private final DomainModelMapper domainModelMapper;
    private final EventPublisher eventPublisher;

    @Override
    public ReservationSchedule createFor(ParkingSpotId parkingSpotId) {
        ReservationsEntity entity = new ReservationsEntity(parkingSpotId.getValue());
        DATABASE.put(parkingSpotId, entity);
        log.debug("creating reservation schedule for parking spot with id {}", parkingSpotId);
        return domainModelMapper.map(entity);
    }

    @Override
    public ReservationSchedule markOccupation(ParkingSpotId parkingSpotId, boolean occupied) {
        ReservationsEntity entity = DATABASE.get(parkingSpotId);
        entity = entity.changeOccupation(occupied);
        return domainModelMapper.map(entity);
    }

    @Override
    public Option<ReservationSchedule> findBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .map(domainModelMapper::map);
    }

    @Override
    public Option<ReservationSchedule> findBy(ReservationId reservationId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(reservationsEntity -> reservationsEntity.collection.stream()
                                .anyMatch(reservationEntity -> reservationEntity.reservationId.equals(reservationId.getValue())))
                        .findFirst()
                        .map(domainModelMapper::map));
    }

    @Override
    public Option<ReservationSchedule> findFreeFor(ReservationSlot reservationSlot) {
        return Option.ofOptional(
                DATABASE.values().stream()
                        .filter(reservationsEntity -> reservationsEntity.noOccupation)
                        .findFirst()
                        .map(domainModelMapper::map));
    }

    @Override
    public ReservationSchedule publish(ReservationScheduleEvent event) {
        ReservationSchedule result = handle(event);
        eventPublisher.publish(event.normalize());
        return result;
    }

    private ReservationSchedule handle(ReservationScheduleEvent event) {
        ReservationsEntity entity = DATABASE.get(event.getParkingSpotId());
        entity = entity.handle(event);
        DATABASE.put(event.getParkingSpotId(), entity);
        return domainModelMapper.map(entity);
    }

}
