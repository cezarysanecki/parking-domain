package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import io.vavr.API;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.application.FindingParkingSpotReservations;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpots;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ReservationId;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

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
    public Option<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
        return Option.of(DATABASE.get(parkingSpotId))
                .map(DomainModelMapper::map);
    }

    @Override
    public Option<ParkingSpot> findBy(VehicleId vehicleId) {
        return Option.ofOptional(
                        DATABASE.values().stream()
                                .filter(entity -> entity.vehicles.stream()
                                        .anyMatch(vehicle -> vehicle.vehicleId.equals(vehicleId.getValue())))
                                .findFirst())
                .map(DomainModelMapper::map);
    }

    @Override
    public void publish(ParkingSpotEvent domainEvent) {
        Match(domainEvent).of(
                API.Case(API.$(instanceOf(ParkingSpotAdded.class)), this::createNewParkingSpot),
                Case($(), this::handleNextEvent));
        eventPublisher.publish(domainEvent.normalize());
    }

    private ParkingSpotEvent createNewParkingSpot(ParkingSpotAdded domainEvent) {
        ParkingSpotEntity entity = new ParkingSpotEntity(
                domainEvent.parkingSpotId().getValue(),
                domainEvent.capacity().getValue(),
                new HashSet<>());
        DATABASE.put(domainEvent.parkingSpotId(), entity);
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
