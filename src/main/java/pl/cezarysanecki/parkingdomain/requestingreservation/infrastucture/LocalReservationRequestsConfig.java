package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.List;
import io.vavr.control.Option;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequester;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequests;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestsEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestsRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplate;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.template.ReservationRequestsTemplateRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequesterViewRepository;
import pl.cezarysanecki.parkingdomain.requestingreservation.web.ReservationRequestsViewRepository;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalReservationRequestsConfig {

  @Bean
  InMemoryTemplateRepository inMemoryTemplateRepository() {
    return new InMemoryTemplateRepository();
  }

  @Bean
  InMemoryRequesterRepository inMemoryRequesterRepository() {
    return new InMemoryRequesterRepository();
  }

  @Bean
  InMemoryTimeSlotRepository inMemoryTimeSlotRepository() {
    return new InMemoryTimeSlotRepository();
  }

  @Bean
  InMemoryReservationRequestsRepository inMemoryReservationRequestsRepository(
      InMemoryRequesterRepository inMemoryRequesterRepository,
      InMemoryTimeSlotRepository inMemoryTimeSlotRepository
  ) {
    return new InMemoryReservationRequestsRepository(
        inMemoryRequesterRepository,
        inMemoryTimeSlotRepository);
  }

  @Bean
  InMemoryReservationRequestsViewRepository inMemoryReservationRequestsViewRepository() {
    return new InMemoryReservationRequestsViewRepository();
  }

  @Bean
  InMemoryReservationRequesterViewRepository inMemoryReservationRequesterViewRepository() {
    return new InMemoryReservationRequesterViewRepository();
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

    static ReservationRequestsTemplate joinBy(ReservationRequestsTemplateId templateId) {
      return Option.of(DATABASE.get(templateId))
          .getOrElseThrow(() -> new IllegalStateException("cannot find template with id " + templateId));
    }

  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class InMemoryReservationRequests {

    static final Map<ReservationRequestId, ReservationRequest> DATABASE = new ConcurrentHashMap<>();

    static java.util.List<ReservationRequest> joinBy(ReservationRequestsTimeSlotId timeSlotId) {
      return DATABASE.values()
          .stream()
          .filter(reservationRequest -> reservationRequest.reservationRequestsTimeSlotId().equals(timeSlotId))
          .toList();
    }

  }

  @RequiredArgsConstructor
  static class InMemoryRequesterRepository implements ReservationRequesterRepository {

    static final Map<ReservationRequesterId, ReservationRequesterEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void publish(ReservationRequesterEvent event) {
      if (event instanceof ReservationRequesterEvent.ReservationRequestCreated created) {
        DATABASE.put(created.requesterId(), new ReservationRequesterEntity(
            created.requesterId().getValue(),
            new HashSet<>(),
            created.limit(),
            0));
        return;
      }

      ReservationRequesterEntity entity = DATABASE.get(event.requesterId());
      if (entity == null) {
        throw new EntityNotFoundException("cannot find requester by id " + event.requesterId());
      }
      entity.handle(event);
    }

    @Override
    public Option<ReservationRequester> findBy(ReservationRequesterId requesterId) {
      return Option.of(DATABASE.get(requesterId))
          .map(ReservationRequesterEntity::toDomain);
    }

    @Override
    public Option<ReservationRequester> findBy(ReservationRequestId reservationRequestId) {
      return Option.ofOptional(
              DATABASE.values()
                  .stream()
                  .filter(requester -> requester.currentRequests.contains(reservationRequestId.getValue()))
                  .findFirst())
          .map(ReservationRequesterEntity::toDomain);
    }

  }

  @RequiredArgsConstructor
  static class InMemoryTimeSlotRepository implements ReservationRequestsTimeSlotRepository {

    static final Map<ReservationRequestsTimeSlotId, ReservationRequestsTimeSlotEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void publish(ReservationRequestsTimeSlotEvent event) {
      if (event instanceof ReservationRequestsTimeSlotEvent.ReservationRequestCreated created) {
        ReservationRequestsTemplate template = InMemoryTemplateRepository.joinBy(created.templateId());
        DATABASE.put(created.timeSlotId(), new ReservationRequestsTimeSlotEntity(
            created.timeSlotId().getValue(),
            template.capacity().getValue(),
            new HashSet<>(),
            0,
            created.templateId().getValue(),
            created.timeSlot()));
        return;
      }

      ReservationRequestsTimeSlotEntity entity = DATABASE.get(event.timeSlotId());
      entity.handle(event);
    }

    @Override
    public Option<ReservationRequestsTimeSlot> findBy(ReservationRequestsTimeSlotId reservationRequestsTimeSlotId) {
      return Option.of(DATABASE.get(reservationRequestsTimeSlotId))
          .map(entity -> {
            java.util.List<ReservationRequest> reservationRequests = InMemoryReservationRequests.joinBy(
                ReservationRequestsTimeSlotId.of(entity.timeSlotId));
            return entity.toDomain(reservationRequests);
          });
    }

    @Override
    public Option<ReservationRequestsTimeSlot> findBy(ReservationRequestId reservationRequestId) {
      return Option.ofOptional(
              DATABASE.values()
                  .stream()
                  .filter(entity -> entity.reservationRequests
                      .stream()
                      .anyMatch(currentReservationRequestId -> currentReservationRequestId.equals(reservationRequestId.getValue())))
                  .findFirst())
          .map(entity -> {
            java.util.List<ReservationRequest> reservationRequests = InMemoryReservationRequests.joinBy(
                ReservationRequestsTimeSlotId.of(entity.timeSlotId));
            return entity.toDomain(reservationRequests);
          });
    }

    @Override
    public boolean containsAny() {
      return !DATABASE.isEmpty();
    }

    static ReservationRequestsTimeSlotEntity joinBy(ReservationRequestId reservationRequestId) {
      return DATABASE.values()
          .stream()
          .filter(entity -> entity.reservationRequests.stream()
              .anyMatch(currentReservationRequestId -> currentReservationRequestId.equals(reservationRequestId.getValue())))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("cannot find time slot for reservation request with id " + reservationRequestId));
    }

  }

  @RequiredArgsConstructor
  static class InMemoryReservationRequestsRepository implements ReservationRequestsRepository {

    private final InMemoryRequesterRepository requesterRepository;
    private final InMemoryTimeSlotRepository timeSlotRepository;

    @Override
    public void publish(ReservationRequestsEvent event) {
      switch (event) {
        case ReservationRequestsEvent.ReservationRequestMade made -> {
          requesterRepository.publish(made.requesterEvent());
          timeSlotRepository.publish(made.timeSlotEvent());
        }
        case ReservationRequestsEvent.ReservationRequestCancelled cancelled -> {
          requesterRepository.publish(cancelled.requesterEvent());
          timeSlotRepository.publish(cancelled.timeSlotEvent());

        }
        default -> {
        }
      }
    }

    @Override
    public Option<ReservationRequests> findBy(ReservationRequesterId requesterId, ReservationRequestsTimeSlotId timeSlotId) {
      Option<ReservationRequester> requester = requesterRepository.findBy(requesterId);
      Option<ReservationRequestsTimeSlot> timeSlot = timeSlotRepository.findBy(timeSlotId);
      if (requester.isEmpty() || timeSlot.isEmpty()) {
        return Option.none();
      }
      return Option.of(new ReservationRequests(timeSlot.get(), requester.get()));
    }

    @Override
    public Option<ReservationRequests> findBy(ReservationRequestId reservationRequestId) {
      Option<ReservationRequester> requester = requesterRepository.findBy(reservationRequestId);
      Option<ReservationRequestsTimeSlot> timeSlot = timeSlotRepository.findBy(reservationRequestId);
      if (requester.isEmpty() || timeSlot.isEmpty()) {
        return Option.none();
      }
      return Option.of(new ReservationRequests(timeSlot.get(), requester.get()));
    }

  }

  @RequiredArgsConstructor
  static class InMemoryReservationRequestsViewRepository implements ReservationRequestsViewRepository {

    @Override
    public java.util.List<ParkingSpotReservationRequestsView> queryForAllAvailableParkingSpots() {
      return InMemoryTimeSlotRepository.DATABASE.values()
          .stream()
          .map(timeSlot -> {
            ReservationRequestsTemplate template = InMemoryTemplateRepository.joinBy(ReservationRequestsTemplateId.of(timeSlot.templateId));
            java.util.List<ReservationRequest> reservationRequests = InMemoryReservationRequests.joinBy(ReservationRequestsTimeSlotId.of(timeSlot.timeSlotId));

            return new ParkingSpotReservationRequestsView(
                template.parkingSpotId().getValue(),
                timeSlot.timeSlotId,
                template.category(),
                timeSlot.timeSlot,
                timeSlot.capacity,
                timeSlot.capacity - reservationRequests.stream()
                    .map(ReservationRequest::spotUnits)
                    .map(SpotUnits::getValue)
                    .reduce(0, Integer::sum),
                reservationRequests.stream()
                    .map(ReservationRequest::reservationRequestId)
                    .map(ReservationRequestId::getValue)
                    .toList()
            );
          })
          .toList();
    }

  }

  @RequiredArgsConstructor
  static class InMemoryReservationRequesterViewRepository implements ReservationRequesterViewRepository {

    @Override
    public java.util.List<ReservationRequesterView> queryForAllReservationRequesters() {
      return InMemoryRequesterRepository.DATABASE.values()
          .stream()
          .map(requester -> new ReservationRequesterView(
              requester.requesterId,
              requester.currentRequests.stream()
                  .map(request -> {
                    ReservationRequestsTimeSlotEntity timeSlotEntity = InMemoryTimeSlotRepository.joinBy(ReservationRequestId.of(request));
                    ReservationRequestsTemplate templateEntity = InMemoryTemplateRepository.joinBy(ReservationRequestsTemplateId.of(timeSlotEntity.templateId));

                    return new ReservationRequesterView.ReservationRequestView(
                        request,
                        templateEntity.parkingSpotId().getValue(),
                        timeSlotEntity.timeSlot);
                  })
                  .toList()))
          .toList();
    }

  }

}
