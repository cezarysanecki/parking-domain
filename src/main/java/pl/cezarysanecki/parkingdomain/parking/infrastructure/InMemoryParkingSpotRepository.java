package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.parking.model.parking.OpenParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parking.OpenParkingSpotFactory;
import pl.cezarysanecki.parkingdomain.parking.model.parking.ReservedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.parking.ReservedParkingSpotFactory;
import pl.cezarysanecki.parkingdomain.parking.model.releasing.OccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.releasing.OccupiedParkingSpotFactory;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

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
    public Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
        return find(entity -> entity.parkingSpotId.equals(parkingSpotId.getValue()))
                .map(entity -> entity.reservation
                        .map(reservationId -> ReservedParkingSpotFactory.create(
                                DomainModelMapper.map(entity), ReservationId.of(reservationId)))
                        .map(ParkingSpot.class::cast)
                        .getOrElse(() -> OpenParkingSpotFactory.create(DomainModelMapper.map(entity))));
    }

    @Override
    public Option<OpenParkingSpot> findBy(VehicleSizeUnit vehicleSizeUnit) {
        return find(entity -> {
            Integer currentOccupation = entity.parkedVehicles.stream()
                    .map(ParkedVehicleEntity::getVehicleSizeUnit)
                    .reduce(0, Integer::sum);
            boolean hasEnoughSpace = entity.capacity - currentOccupation >= vehicleSizeUnit.getValue();

            return entity.reservation.isEmpty() && hasEnoughSpace;
        }).map(DomainModelMapper::map).map(OpenParkingSpotFactory::create);
    }

    @Override
    public Option<ReservedParkingSpot> findBy(ReservationId reservationId) {
        return find(entity -> entity.reservation.isDefined()
                && ReservationId.of(entity.reservation.get()).equals(reservationId)
        ).map(DomainModelMapper::map).map(parkingSpotBase -> ReservedParkingSpotFactory.create(
                parkingSpotBase,
                reservationId));
    }

    @Override
    public Option<OccupiedParkingSpot> findBy(VehicleId vehicleId) {
        return find(entity -> entity.parkedVehicles.stream()
                .map(ParkedVehicleEntity::getVehicleId)
                .anyMatch(parkedVehicleId -> parkedVehicleId.equals(vehicleId.getValue()))
        ).map(DomainModelMapper::map).map(OccupiedParkingSpotFactory::create);
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
        return OpenParkingSpotFactory.create(DomainModelMapper.map(entity));
    }

    private ParkingSpot handleNextEvent(ParkingSpotEvent event) {
        ParkingSpotEntity entity = DATABASE.get(event.getParkingSpotId());
        entity = entity.handle(event);
        DATABASE.put(event.getParkingSpotId(), entity);

        ParkingSpotEntity finalEntity = entity;
        return entity.reservation
                .map(reservationId -> (ParkingSpot) ReservedParkingSpotFactory.create(
                        DomainModelMapper.map(finalEntity),
                        ReservationId.of(reservationId)))
                .getOrElse(() -> OpenParkingSpotFactory.create(DomainModelMapper.map(finalEntity)));
    }

    private Option<ParkingSpotEntity> find(Predicate<ParkingSpotEntity> filter) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(filter)
                        .findFirst());
    }

}
