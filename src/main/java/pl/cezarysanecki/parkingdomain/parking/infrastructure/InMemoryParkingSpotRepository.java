package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.OccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.OpenParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotFactory;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.model.ReservedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotRepository implements ParkingSpots {

    private static final Map<ParkingSpotId, ParkingSpotEntity> DATABASE = new ConcurrentHashMap<>();
    private final EventPublisher eventPublisher;

    @Override
    public Option<OpenParkingSpot> findBy(ParkingSpotId parkingSpotId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(parkingSpot -> ParkingSpotId.of(parkingSpot.parkingSpotId).equals(parkingSpotId))
                        .findFirst()
                        .map(DomainModelMapper::map)
                        .map(ParkingSpotFactory::createOpen));
    }

    @Override
    public Option<ReservedParkingSpot> findBy(ReservationId reservationId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(parkingSpot -> parkingSpot.reservation.isDefined())
                        .filter(parkingSpot -> ReservationId.of(parkingSpot.reservation.get()).equals(reservationId))
                        .findFirst()
                        .map(entity -> ParkingSpotFactory.createReserved(
                                DomainModelMapper.map(entity),
                                ReservationId.of(entity.reservation.get())
                        )));
    }

    @Override
    public Option<OccupiedParkingSpot> findBy(VehicleId vehicleId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(entity -> entity.parkedVehicles.stream()
                                .map(ParkedVehicleEntity::getVehicleId)
                                .anyMatch(parkedVehicleId -> parkedVehicleId.equals(vehicleId.getValue())))
                        .findFirst()
                        .map(DomainModelMapper::map)
                        .map(ParkingSpotFactory::createOccupied));
    }

    @Override
    public ParkingSpot publish(ParkingSpotEvent event) {
        ParkingSpot result = Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::createNewParkingSpot),
                Case($(), this::handleNextEvent));
        eventPublisher.publish(event.normalize());
        return result;
    }

    private ParkingSpot createNewParkingSpot(ParkingSpotCreated event) {
        ParkingSpotId parkingSpotId = event.getParkingSpotId();
        ParkingSpotEntity entity = new ParkingSpotEntity(parkingSpotId.getValue(), event.getCapacity());
        DATABASE.put(parkingSpotId, entity);
        log.debug("creating parking spot with id {}", parkingSpotId);
        return ParkingSpotFactory.createOccupied(DomainModelMapper.map(entity));
    }

    private ParkingSpot handleNextEvent(ParkingSpotEvent event) {
        ParkingSpotEntity entity = DATABASE.get(event.getParkingSpotId());
        entity = entity.handle(event);
        DATABASE.put(event.getParkingSpotId(), entity);

        ParkingSpotEntity finalEntity = entity;
        return entity.reservation
                .map(reservationId -> (ParkingSpot) ParkingSpotFactory.createReserved(
                        DomainModelMapper.map(finalEntity),
                        ReservationId.of(reservationId)))
                .getOrElse(() -> ParkingSpotFactory.createOpen(DomainModelMapper.map(finalEntity)));
    }

}
