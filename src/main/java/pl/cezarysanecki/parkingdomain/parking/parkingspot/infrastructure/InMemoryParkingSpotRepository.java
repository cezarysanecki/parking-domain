package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.CreatingParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.FindingParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.OccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.OpenParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;
import pl.cezarysanecki.parkingdomain.requestingreservation.client.model.ReservationId;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

@RequiredArgsConstructor
class InMemoryParkingSpotRepository implements ParkingSpots, FindingParkingSpotReservations {

    private static final Map<ParkingSpotId, ParkingSpotEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public Option<ParkingSpotId> findParkingSpotIdByAssigned(ReservationId reservationId) {
        return Option.none();
    }

    @Override
    public Option<OpenParkingSpot> findOpenBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .map(DomainModelMapper::mapOpen);
    }

    @Override
    public Option<OccupiedParkingSpot> findOccupiedBy(VehicleId vehicleId) {
        return Option.ofOptional(
                        DATABASE.values().stream()
                                .filter(entity -> entity.vehicles.stream()
                                        .anyMatch(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue())))
                                .findFirst())
                .map(DomainModelMapper::mapOccupied);
    }

    @Override
    public void publish(ParkingSpotEvent domainEvent) {
        Match(domainEvent).of(
                API.Case(API.$(instanceOf(CreatingParkingSpot.ParkingSpotCreated.class)), this::createNewParkingSpot),
                Case($(), this::handleNextEvent));
        eventPublisher.publish(domainEvent.normalize());
    }

    private ParkingSpotEvent createNewParkingSpot(CreatingParkingSpot.ParkingSpotCreated domainEvent) {
        ParkingSpotEntity entity = new ParkingSpotEntity(
                domainEvent.getParkingSpotId().getValue(),
                domainEvent.getParkingSpotCapacity().getValue(),
                new HashSet<>());
        DATABASE.put(domainEvent.getParkingSpotId(), entity);
        return domainEvent;
    }

    private ParkingSpotEvent handleNextEvent(ParkingSpotEvent domainEvent) {
        ParkingSpotEntity entity = DATABASE.get(domainEvent.getParkingSpotId());
        if (entity == null) {
            return domainEvent;
        }
        entity.handle(domainEvent);
        return domainEvent;
    }

}
