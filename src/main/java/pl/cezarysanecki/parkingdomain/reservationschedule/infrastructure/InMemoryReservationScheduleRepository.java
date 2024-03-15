package pl.cezarysanecki.parkingdomain.reservationschedule.infrastructure;

import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationScheduleEvent;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedule;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSchedules;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationSlot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@RequiredArgsConstructor
class InMemoryReservationScheduleRepository implements ReservationSchedules {

    private static final Map<ParkingSpotId, ReservationsEntity> DATABASE = new ConcurrentHashMap<>();
    private final DomainModelMapper domainModelMapper;
    private final EventPublisher eventPublisher;

    @EventListener
    public void handle(ParkingSpotEvent event) {
        API.Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::handle),
                Case($(), this::handleNextEvent));
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

    private ReservationSchedule handle(ParkingSpotCreated parkingSpotCreated) {
        ParkingSpotId parkingSpotId = parkingSpotCreated.getParkingSpotId();
        ReservationsEntity entity = new ReservationsEntity(parkingSpotId.getValue());
        DATABASE.put(parkingSpotId, entity);
        log.debug("creating reservation schedule for parking spot with id {}", parkingSpotId);
        return domainModelMapper.map(entity);
    }

    private ReservationSchedule handleNextEvent(ParkingSpotEvent parkingSpotEvent) {
        ParkingSpotId parkingSpotId = parkingSpotEvent.getParkingSpotId();
        ReservationsEntity entity = DATABASE.get(parkingSpotId);
        entity = entity.handle(parkingSpotEvent);
        return domainModelMapper.map(entity);
    }

}