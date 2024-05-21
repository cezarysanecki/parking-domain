package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.control.Option;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Predicate.not;
import static pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture.InMemoryParkingSpotReservationRequestsRepository.ReservationRequestsEntity.CurrentRequestEntity;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotReservationRequestsRepository implements
        ParkingSpotReservationRequestsRepository,
        ParkingSpotReservationRequestsViewRepository {

    private static final Map<ParkingSpotTimeSlotId, ReservationRequestsEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void store(NewParkingSpotReservationRequests newOne) {
        ParkingSpotTimeSlotId parkingSpotTimeSlotId = ParkingSpotTimeSlotId.newOne();

        ReservationRequestsEntity entity = new ReservationRequestsEntity(
                newOne.parkingSpotId().getValue(),
                parkingSpotTimeSlotId.getValue(),
                newOne.parkingSpotCategory(),
                newOne.capacity().getValue(),
                new ArrayList<>(),
                newOne.timeSlot().from(),
                newOne.timeSlot().to(),
                Version.zero().getVersion());

        DATABASE.put(parkingSpotTimeSlotId, entity);
    }

    @Override
    public void save(ParkingSpotReservationRequests reservationRequests) {
        ReservationRequestsEntity entity = DATABASE.get(reservationRequests.getParkingSpotTimeSlotId());
        if (entity == null) {
            throw new EntityNotFoundException("there is no entity for parking spot reservation request for time slot id " + reservationRequests.getParkingSpotTimeSlotId());
        }

        entity.currentRequests = reservationRequests.getReservationRequests()
                .values()
                .map(reservationRequest -> new CurrentRequestEntity(
                        reservationRequest.getReservationRequestId().getValue(),
                        reservationRequest.getReservationRequesterId().getValue(),
                        reservationRequest.getSpotUnits().getValue()
                ))
                .toJavaList();

        DATABASE.put(reservationRequests.getParkingSpotTimeSlotId(), entity);
    }

    @Override
    public Option<ParkingSpotReservationRequests> findBy(ParkingSpotTimeSlotId parkingSpotTimeSlotId) {
        return Option.of(DATABASE.get(parkingSpotTimeSlotId))
                .map(DomainMapper::map);
    }

    @Override
    public Option<ParkingSpotReservationRequests> findBy(ParkingSpotCategory parkingSpotCategory, TimeSlot timeSlot) {
        return Option.ofOptional(
                        DATABASE.values()
                                .stream()
                                .filter(entity -> entity.category == parkingSpotCategory
                                        && timeSlot.within(new TimeSlot(entity.from, entity.to)))
                                .findFirst())
                .map(DomainMapper::map);
    }

    @Override
    public Option<ParkingSpotReservationRequests> findBy(ReservationRequestId reservationRequestId) {
        return Option.ofOptional(
                        DATABASE.values()
                                .stream()
                                .filter(reservationRequests -> reservationRequests.currentRequests.stream()
                                        .anyMatch(currentRequest -> currentRequest.reservationRequestId.equals(reservationRequestId.getValue())))
                                .findFirst())
                .map(DomainMapper::map);
    }

    @Override
    public io.vavr.collection.List<ParkingSpotReservationRequests> findAllWithRequests() {
        return io.vavr.collection.List.ofAll(
                        DATABASE.values()
                                .stream()
                                .filter(not(entity -> entity.currentRequests.isEmpty())))
                .map(DomainMapper::map);
    }

    @Override
    public List<ParkingSpotReservationRequestsView> queryForAllAvailableParkingSpots() {
        return DATABASE.values()
                .stream()
                .filter(entity -> entity.spaceLeft() > 0)
                .map(entity -> new ParkingSpotReservationRequestsView(
                        entity.parkingSpotId,
                        entity.parkingSpotTimeSlotId,
                        entity.category,
                        new TimeSlot(entity.from, entity.to),
                        entity.capacity,
                        entity.spaceLeft()
                ))
                .toList();
    }

    @AllArgsConstructor
    static class ReservationRequestsEntity {

        UUID parkingSpotId;
        UUID parkingSpotTimeSlotId;
        ParkingSpotCategory category;
        int capacity;
        List<CurrentRequestEntity> currentRequests;
        Instant from;
        Instant to;
        int version;

        int spaceLeft() {
            return capacity - currentRequests.stream().map(currentRequest -> currentRequest.units).reduce(0, Integer::sum);
        }

        @AllArgsConstructor
        static class CurrentRequestEntity {

            UUID reservationRequestId;
            UUID requesterId;
            int units;

        }

    }

}
