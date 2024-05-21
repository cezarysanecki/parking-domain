package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.control.Option;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ParkingSpotReservationRequestsTemplate;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Predicate.not;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestConfirmed;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestsCreated;

@Slf4j
@RequiredArgsConstructor
class InMemoryParkingSpotReservationRequestsRepository implements
        ParkingSpotReservationRequestsRepository,
        ParkingSpotReservationRequestsViewRepository {

    static final Map<ParkingSpotTimeSlotId, ReservationRequestsEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public void publish(ParkingSpotReservationRequestsEvents event) {
        switch (event) {
            case ReservationRequestsCreated created -> {
                ParkingSpotReservationRequestsTemplate template = InMemoryParkingSpotReservationRequestsTemplateRepository.findBy(created.parkingSpotId());
                DATABASE.put(created.parkingSpotTimeSlotId(), new ReservationRequestsEntity(
                        created.parkingSpotId().getValue(),
                        created.parkingSpotTimeSlotId().getValue(),
                        template.parkingSpotCategory(),
                        template.capacity().getValue(),
                        List.of(),
                        created.timeSlot().from(),
                        created.timeSlot().to(),
                        Version.zero().getVersion()));
            }
            case ReservationRequestConfirmed confirmed -> DATABASE.remove(event.parkingSpotTimeSlotId());
            default -> {
                ReservationRequestsEntity entity = DATABASE.get(event.parkingSpotTimeSlotId());
                if (entity == null) {
                    throw new EntityNotFoundException("there is no entity for parking spot reservation request for time slot with id " + event.parkingSpotTimeSlotId());
                }

                entity = entity.handle(event);
                DATABASE.put(event.parkingSpotTimeSlotId(), entity);
            }
        }

        if (event instanceof DomainEvent domainEvent) {
            eventPublisher.publish(domainEvent);
        }
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
    public io.vavr.collection.List<ParkingSpotReservationRequests> findAllRequestsValidFrom(Instant sinceDate) {
        return io.vavr.collection.List.ofAll(
                        DATABASE.values()
                                .stream()
                                .filter(not(entity -> entity.currentRequests.isEmpty()))
                                .filter(not(entity -> entity.from.isAfter(sinceDate))))
                .map(DomainMapper::map);
    }

    @Override
    public void removeAll() {
        DATABASE.clear();
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

}
