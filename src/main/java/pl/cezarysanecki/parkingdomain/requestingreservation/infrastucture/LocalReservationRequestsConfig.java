package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplate;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateRepository;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ParkingSpotReservationRequestsViewRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequesterViewRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Predicate.not;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEvent.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEvent.ReservationRequestStored;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.ReservationRequestEvent.ReservationRequestsConfirmed;

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalReservationRequestsConfig {

    private final EventPublisher eventPublisher;

    @Bean
    InMemoryReservationRequestEventPublisher inMemoryReservationRequestEventPublisher() {
        return new InMemoryReservationRequestEventPublisher(eventPublisher);
    }

    @Bean
    InMemoryTemplateRepository inMemoryTemplateRepository() {
        return new InMemoryTemplateRepository();
    }

    @Bean
    InMemoryTimeSlotsRepository inMemoryTimeSlotsRepository(
            InMemoryTimeSlotsViewRepository inMemoryTimeSlotsViewRepository
    ) {
        return new InMemoryTimeSlotsRepository(inMemoryTimeSlotsViewRepository);
    }

    @Bean
    InMemoryTimeSlotsViewRepository inMemoryTimeSlotsViewRepository() {
        return new InMemoryTimeSlotsViewRepository();
    }

    @Bean
    InMemoryRequesterRepository inMemoryRequesterRepository() {
        return new InMemoryRequesterRepository();
    }

    @RequiredArgsConstructor
    static class InMemoryReservationRequestEventPublisher implements ReservationRequestEventPublisher {

        private final EventPublisher eventPublisher;

        @Override
        public void publish(ReservationRequestEvent event) {
            ReservationRequestEvent processedEvent = switch (event) {
                case ReservationRequestStored stored -> {
                    ReservationRequestsTimeSlotEntity entity = InMemoryTimeSlotsRepository.DATABASE.get(stored.reservationRequestsTimeSlotId());
                    entity.reservationRequests.add(stored.reservationRequest());
                    yield stored;
                }
                case ReservationRequestCancelled cancelled -> {
                    ReservationRequestsTimeSlotEntity entity = InMemoryTimeSlotsRepository.DATABASE.get(cancelled.reservationRequestsTimeSlotId());
                    entity.reservationRequests.remove(cancelled.reservationRequest());
                    yield cancelled;
                }
                case ReservationRequestsConfirmed confirmed -> {
                    InMemoryTimeSlotsRepository.DATABASE.remove(confirmed.reservationRequestsTimeSlotId());
                    yield confirmed;
                }
                default -> event;
            };

            if (processedEvent instanceof DomainEvent domainEvent) {
                eventPublisher.publish(domainEvent);
            }
        }

    }

    @RequiredArgsConstructor
    static class InMemoryTemplateRepository implements ReservationRequestsTemplateRepository {

        static final Map<ReservationRequestsTemplateId, ReservationRequestsTemplate> DATABASE = new ConcurrentHashMap<>();

        @Override
        public void save(ReservationRequestsTemplate template) {
            DATABASE.put(template.templateId(), template);
        }

        @Override
        public List<ReservationRequestsTemplate> findAll() {
            return List.ofAll(DATABASE.values());
        }

        static ReservationRequestsTemplate findBy(ReservationRequestsTemplateId templateId) {
            return Option.of(DATABASE.get(templateId))
                    .getOrElseThrow(() -> new IllegalStateException("cannot find template with id " + templateId));
        }

    }

    @RequiredArgsConstructor
    static class InMemoryTimeSlotsRepository implements ReservationRequestsTimeSlotsRepository {

        static final Map<ReservationRequestsTimeSlotId, ReservationRequestsTimeSlotEntity> DATABASE = new ConcurrentHashMap<>();

        private final InMemoryTimeSlotsViewRepository viewRepository;

        @Override
        public void saveNewUsing(ReservationRequestsTemplateId reservationRequestsTemplateId, TimeSlot timeSlot) {
            ReservationRequestsTemplate template = InMemoryTemplateRepository.findBy(reservationRequestsTemplateId);

            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId = ReservationRequestsTimeSlotId.newOne();
            DATABASE.put(reservationRequestsTimeSlotId, new ReservationRequestsTimeSlotEntity(
                    template.parkingSpotId().getValue(),
                    reservationRequestsTimeSlotId.getValue(),
                    timeSlot.from(),
                    template.capacity().getValue(),
                    new ArrayList<>(),
                    0));

            viewRepository.saveNewUsing(reservationRequestsTimeSlotId, reservationRequestsTemplateId, timeSlot);
        }

        @Override
        public void save(ReservationRequestsTimeSlot reservationRequestsTimeSlot) {
            DATABASE.put(
                    reservationRequestsTimeSlot.getReservationRequestsTimeSlotId(),
                    ReservationRequestsTimeSlotEntity.from(reservationRequestsTimeSlot));

            Integer occupied = reservationRequestsTimeSlot.getReservationRequests().values().map(ReservationRequest::getSpotUnits).map(SpotUnits::getValue).reduce(Integer::sum);
            viewRepository.updateSpaceLeft(
                    reservationRequestsTimeSlot.getReservationRequestsTimeSlotId(),
                    reservationRequestsTimeSlot.getCapacity().getValue() - occupied);
        }

        @Override
        public Option<ReservationRequestsTimeSlot> findBy(ReservationRequestsTimeSlotId reservationRequestsTimeSlotId) {
            return Option.of(DATABASE.get(reservationRequestsTimeSlotId))
                    .map(ReservationRequestsTimeSlotEntity::toDomain);
        }

        @Override
        public Option<ReservationRequestsTimeSlot> findBy(ReservationRequestId reservationRequestId) {
            return Option.ofOptional(
                            DATABASE.values()
                                    .stream()
                                    .filter(entity -> entity.reservationRequests
                                            .stream()
                                            .anyMatch(currentRequest -> currentRequest.getReservationRequestId().equals(reservationRequestId)))
                                    .findFirst())
                    .map(ReservationRequestsTimeSlotEntity::toDomain);
        }

        @Override
        public List<ReservationRequestsTimeSlot> findAllValidSince(Instant sinceDate) {
            return List.ofAll(
                            DATABASE.values()
                                    .stream()
                                    .filter(not(entity -> entity.validSince.isAfter(sinceDate))))
                    .map(ReservationRequestsTimeSlotEntity::toDomain);
        }

        @Override
        public boolean containsAny() {
            return !DATABASE.isEmpty();
        }

    }

    @RequiredArgsConstructor
    static class InMemoryTimeSlotsViewRepository implements ParkingSpotReservationRequestsViewRepository {

        static final Map<ReservationRequestsTimeSlotId, ViewEntity> DATABASE = new ConcurrentHashMap<>();

        void saveNewUsing(
                ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
                ReservationRequestsTemplateId reservationRequestsTemplateId,
                TimeSlot timeSlot) {
            ReservationRequestsTemplate template = InMemoryTemplateRepository.findBy(reservationRequestsTemplateId);

            DATABASE.put(reservationRequestsTimeSlotId, new ViewEntity(
                    template.parkingSpotId().getValue(),
                    reservationRequestsTimeSlotId.getValue(),
                    template.category(),
                    timeSlot,
                    template.capacity().getValue(),
                    template.capacity().getValue()));
        }

        void updateSpaceLeft(ReservationRequestsTimeSlotId reservationRequestsTimeSlotId, int spaceLeft) {
            ViewEntity entity = DATABASE.get(reservationRequestsTimeSlotId);
            if (entity != null) {
                entity.spaceLeft = spaceLeft;
            }
        }

        @Override
        public java.util.List<ParkingSpotReservationRequestsView> queryForAllAvailableParkingSpots() {
            return DATABASE.values()
                    .stream()
                    .filter(entity -> entity.spaceLeft > 0)
                    .map(entity -> new ParkingSpotReservationRequestsView(
                            entity.parkingSpotId,
                            entity.parkingSpotTimeSlotId,
                            entity.parkingSpotCategory,
                            entity.timeSlot,
                            entity.capacity,
                            entity.spaceLeft))
                    .toList();
        }

        @AllArgsConstructor
        class ViewEntity {

            UUID parkingSpotId;
            UUID parkingSpotTimeSlotId;
            ParkingSpotCategory parkingSpotCategory;
            TimeSlot timeSlot;
            int capacity;
            int spaceLeft;

        }

    }

    @RequiredArgsConstructor
    static class InMemoryRequesterRepository implements ReservationRequesterRepository, ReservationRequesterViewRepository {

        static final Map<ReservationRequesterId, ReservationRequester> DATABASE = new ConcurrentHashMap<>();

        @Override
        public void save(ReservationRequester reservationRequester) {
            DATABASE.put(reservationRequester.getRequesterId(), reservationRequester);
        }

        @Override
        public Option<ReservationRequester> findBy(ReservationRequesterId requesterId) {
            return Option.of(DATABASE.get(requesterId));
        }

        @Override
        public Option<ReservationRequester> findBy(ReservationRequestId reservationRequestId) {
            return Option.ofOptional(
                    DATABASE.values()
                            .stream()
                            .filter(requester -> requester.getReservationRequests().contains(reservationRequestId))
                            .findFirst());
        }

        @Override
        public void removeRequestsFromRequesters(List<ReservationRequestId> reservationRequestIds) {
            reservationRequestIds.forEach(reservationRequestId ->
                    findBy(reservationRequestId)
                            .map(entity -> entity.remove(reservationRequestId)));
        }

        @Override
        public java.util.List<ReservationRequesterView> queryForAllReservationRequesters() {
            return DATABASE.values()
                    .stream()
                    .map(requester -> new ReservationRequesterView(
                            requester.getRequesterId().getValue(),
                            requester.getReservationRequests().map(ReservationRequestId::getValue).toJavaList()
                    ))
                    .toList();
        }

    }

}
