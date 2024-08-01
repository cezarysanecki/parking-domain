package pl.cezarysanecki.parkingdomain.requesting;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.web.ReservationRequesterViewRepository;
import pl.cezarysanecki.parkingdomain.web.ReservationRequestsViewRepository;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Profile("local")
@Configuration
@RequiredArgsConstructor
class LocalReservationRequestsConfig {

  private final EventPublisher eventPublisher;

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
  InMemoryReservationRequestsRepository inMemoryReservationRequestsRepository() {
    return new InMemoryReservationRequestsRepository();
  }

  @Bean
  InMemoryReservationRequestRepository inMemoryReservationRequestRepository() {
    return new InMemoryReservationRequestRepository(eventPublisher);
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

  @RequiredArgsConstructor
  static class InMemoryRequesterRepository implements ReservationRequesterRepository {

    static final Map<RequesterId, ReservationRequesterEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void saveNew(RequesterId requesterId, int limit) {
      DATABASE.put(requesterId, new ReservationRequesterEntity(
          requesterId.getValue(),
          limit,
          0));
    }

    @Override
    public Option<ReservationRequester> findBy(RequesterId requesterId) {
      return Option.of(DATABASE.get(requesterId))
          .map(entity -> {
            java.util.List<ReservationRequestEntity> reservationRequests = InMemoryReservationRequestRepository.joinBy(requesterId);
            return entity.toDomain(reservationRequests);
          });
    }

    static ReservationRequesterEntity joinBy(RequesterId requesterId) {
      return Option.of(DATABASE.get(requesterId))
          .getOrElseThrow(() -> new IllegalStateException("cannot find requester with id " + requesterId));
    }

  }

  @RequiredArgsConstructor
  static class InMemoryTimeSlotRepository implements ReservationRequestsTimeSlotRepository {

    static final Map<ReservationRequestsTimeSlotId, ReservationRequestsTimeSlotEntity> DATABASE = new ConcurrentHashMap<>();

    @Override
    public void saveNew(ReservationRequestsTimeSlotId timeSlotId, ReservationRequestsTemplateId templateId, TimeSlot timeSlot) {
      ReservationRequestsTemplate template = InMemoryTemplateRepository.joinBy(templateId);

      DATABASE.put(timeSlotId, new ReservationRequestsTimeSlotEntity(
          timeSlotId.getValue(),
          template.capacity().getValue(),
          timeSlot.from(),
          timeSlot.to(),
          templateId.getValue(),
          0));
    }

    @Override
    public Option<ReservationRequestsTimeSlot> findBy(ReservationRequestsTimeSlotId timeSlotId) {
      return Option.of(DATABASE.get(timeSlotId))
          .map(entity -> {
            java.util.List<ReservationRequestEntity> reservationRequests = InMemoryReservationRequestRepository.joinBy(
                ReservationRequestsTimeSlotId.of(entity.timeSlotId));
            return entity.toDomain(reservationRequests);
          });
    }

    @Override
    public boolean containsAny() {
      return !DATABASE.isEmpty();
    }

    @Override
    public void removeAllValidSince(Instant date) {
      DATABASE.values()
          .removeIf(entity -> date.isAfter(entity.from));
    }

    static ReservationRequestsTimeSlotEntity joinBy(ReservationRequestsTimeSlotId timeSlotId) {
      return Option.of(DATABASE.get(timeSlotId))
          .getOrElseThrow(() -> new IllegalStateException("cannot find time slot with id " + timeSlotId));
    }

  }

  @RequiredArgsConstructor
  static class InMemoryReservationRequestsRepository implements ReservationRequestsRepository {

    @Override
    public void publish(ReservationRequestsEvent event) {
      if (event instanceof ReservationRequestsEvent.ReservationRequestMade requestMade) {
        ReservationRequest reservationRequest = requestMade.reservationRequest();

        InMemoryReservationRequestRepository.DATABASE.put(reservationRequest.getReservationRequestId(), new ReservationRequestEntity(
            reservationRequest.getReservationRequestId().getValue(),
            reservationRequest.getRequesterId().getValue(),
            reservationRequest.getTimeSlotId().getValue(),
            reservationRequest.getSpotUnits().getValue()
        ));
      }
    }

    @Override
    public Option<ReservationRequests> findBy(RequesterId requesterId, ReservationRequestsTimeSlotId timeSlotId) {
      ReservationRequesterEntity requester = InMemoryRequesterRepository.joinBy(requesterId);
      ReservationRequestsTimeSlotEntity timeSlot = InMemoryTimeSlotRepository.joinBy(timeSlotId);

      java.util.List<ReservationRequestEntity> requesterReservationRequests = InMemoryReservationRequestRepository.joinBy(requesterId);
      java.util.List<ReservationRequestEntity> timeSlotsReservationRequests = InMemoryReservationRequestRepository.joinBy(timeSlotId);

      return Option.of(new ReservationRequests(
          timeSlot.toDomain(timeSlotsReservationRequests),
          requester.toDomain(requesterReservationRequests)));
    }

  }

  @RequiredArgsConstructor
  static class InMemoryReservationRequestRepository implements ReservationRequestRepository {

    static final Map<ReservationRequestId, ReservationRequestEntity> DATABASE = new ConcurrentHashMap<>();

    private final EventPublisher eventPublisher;

    @Override
    public void publish(ReservationRequestEvent event) {
      if (event instanceof ReservationRequestEvent.ReservationRequestCancelled cancelled) {
        DATABASE.remove(cancelled.reservationRequest().getReservationRequestId());
      } else if (event instanceof ReservationRequestEvent.ReservationRequestConfirmed confirmed) {
        ReservationRequest reservationRequest = confirmed.reservationRequest();
        ReservationRequestId reservationRequestId = reservationRequest.getReservationRequestId();

        DATABASE.remove(reservationRequestId);

        ReservationRequestsTimeSlotEntity timeSlot = InMemoryTimeSlotRepository.joinBy(reservationRequest.getTimeSlotId());
        ReservationRequestsTemplate template = InMemoryTemplateRepository.joinBy(ReservationRequestsTemplateId.of(timeSlot.templateId));

        eventPublisher.publish(new ReservationRequestsConfirmed(
            template.parkingSpotId(),
            reservationRequest));
      }
    }

    @Override
    public Option<ReservationRequest> findBy(ReservationRequestId reservationRequestId) {
      return Option.of(DATABASE.get(reservationRequestId))
          .map(ReservationRequestEntity::toDomain);
    }

    @Override
    public List<ReservationRequest> findAllValidSince(Instant date) {
      return List.ofAll(
          DATABASE.values()
              .stream()
              .filter(entity -> {
                ReservationRequestsTimeSlotEntity timeSlot = InMemoryTimeSlotRepository.joinBy(
                    ReservationRequestsTimeSlotId.of(entity.timeSlotId));
                return date.isAfter(timeSlot.from);
              })
              .map(ReservationRequestEntity::toDomain)
              .toList());
    }

    static java.util.List<ReservationRequestEntity> joinBy(RequesterId requesterId) {
      return DATABASE.values()
          .stream()
          .filter(reservationRequest -> reservationRequest.requesterId.equals(requesterId.getValue()))
          .toList();
    }

    static java.util.List<ReservationRequestEntity> joinBy(ReservationRequestsTimeSlotId timeSlotId) {
      return DATABASE.values()
          .stream()
          .filter(reservationRequest -> reservationRequest.timeSlotId.equals(timeSlotId.getValue()))
          .toList();
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
            java.util.List<ReservationRequestEntity> reservationRequests = InMemoryReservationRequestRepository.joinBy(ReservationRequestsTimeSlotId.of(timeSlot.timeSlotId));

            return new ParkingSpotReservationRequestsView(
                template.parkingSpotId().getValue(),
                timeSlot.timeSlotId,
                template.category(),
                new TimeSlot(timeSlot.from, timeSlot.to),
                timeSlot.capacity,
                timeSlot.capacity - reservationRequests.stream()
                    .map(reservationRequest -> reservationRequest.requestedUnits)
                    .reduce(0, Integer::sum),
                reservationRequests.stream()
                    .map(reservationRequest -> reservationRequest.reservationRequestId)
                    .toList());
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
          .map(requester -> {
            java.util.List<ReservationRequestEntity> reservationRequests = InMemoryReservationRequestRepository.joinBy(
                RequesterId.of(requester.requesterId));
            return new ReservationRequesterView(
                requester.requesterId,
                reservationRequests.stream()
                    .map(reservationRequest -> {
                      ReservationRequestsTimeSlotEntity timeSlotEntity = InMemoryTimeSlotRepository.joinBy(
                          ReservationRequestsTimeSlotId.of(reservationRequest.timeSlotId));
                      ReservationRequestsTemplate templateEntity = InMemoryTemplateRepository.joinBy(
                          ReservationRequestsTemplateId.of(timeSlotEntity.templateId));

                      return new ReservationRequesterView.ReservationRequestView(
                          reservationRequest.reservationRequestId,
                          templateEntity.parkingSpotId().getValue(),
                          new TimeSlot(timeSlotEntity.from, timeSlotEntity.to));
                    })
                    .toList());
          })
          .toList();
    }

  }

}
