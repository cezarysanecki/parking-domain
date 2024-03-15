package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ParkingSpotCreated;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;

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
    public Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(parkingSpot -> ParkingSpotId.of(parkingSpot.parkingSpotId).equals(parkingSpotId))
                        .findFirst()
                        .map(DomainModelMapper::map));
    }

    @Override
    public Option<ParkingSpot> findBy(VehicleId vehicleId) {
        return Option.ofOptional(
                DATABASE.values()
                        .stream()
                        .filter(entity -> entity.parkedVehicles.stream()
                                .map(ParkedVehicleEntity::getVehicleId)
                                .anyMatch(parkedVehicleId -> parkedVehicleId.equals(vehicleId.getValue())))
                        .findFirst()
                        .map(DomainModelMapper::map));
    }

    @Override
    public ParkingSpot publish(ParkingSpotEvent event) {
        ParkingSpot result = Match(event).of(
                Case($(instanceOf(ParkingSpotCreated.class)), this::createNewParkingSpot),
                Case($(), this::handleNextEvent));
        eventPublisher.publish(event.normalize());
        return result;
    }

    @EventListener
    public void handle(ReservingParkingSpots.ReservationHasBecomeEffective event) {
        log.debug("making reservation to be effective for parking spot with id {}", event.getParkingSpotId());

        ParkingSpotId parkingSpotId = event.getParkingSpotId();

        ParkingSpotEntity entity = DATABASE.get(parkingSpotId);
        entity.reservation = Option.of(new ParkingSpotReservationEntity(
                event.getClientId().getValue(),
                event.getReservationId().getValue()));
        DATABASE.put(parkingSpotId, entity);
    }

    private ParkingSpot createNewParkingSpot(ParkingSpotCreated event) {
        ParkingSpotId parkingSpotId = event.getParkingSpotId();
        ParkingSpotEntity entity = new ParkingSpotEntity(parkingSpotId.getValue(), event.getCapacity());
        DATABASE.put(parkingSpotId, entity);
        log.debug("creating parking spot with id {}", parkingSpotId);
        return DomainModelMapper.map(entity);
    }

    private ParkingSpot handleNextEvent(ParkingSpotEvent event) {
        ParkingSpotEntity entity = DATABASE.get(event.getParkingSpotId());
        entity = entity.handle(event);
        DATABASE.put(event.getParkingSpotId(), entity);
        return DomainModelMapper.map(entity);
    }

}
