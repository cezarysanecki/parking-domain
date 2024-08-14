package pl.cezarysanecki.parkingdomain.requesting;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.MadeRequestsValid;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RequestingFacade {

  private final RequestableParkingSpotRepository requestableSectionRepository;
  private final RequesterRepository requesterRepository;
  private final RequestRepository requestRepository;
  private final EventPublisher eventPublisher;

  @Transactional
  public Optional<RequestId> request(
      RequesterId requesterId,
      ParkingSpotId parkingSpotId,
      TimeSlot timeSlot,
      SpotUnits spotUnits
  ) {
    log.debug("requesting parking spot with id {} by {} units for {} time slot", parkingSpotId, spotUnits, timeSlot);
    RequestableParkingSpot requestableParkingSpot = requestableSectionRepository.findFor(parkingSpotId, timeSlot);
    Requester requester = requesterRepository.findBy(requesterId);

    RequestId requestId = RequestId.newOne();
    if (!requestableParkingSpot.requestFor(spotUnits) || !requester.append(requestId)) {
      log.debug("failed to request parking spot with id {}", parkingSpotId);
      return Optional.empty();
    }
    requestRepository.saveCheckingVersion(new Request(
        requestId, requester, parkingSpotId, timeSlot, spotUnits, requestableParkingSpot
    ));
    return Optional.of(requestId);
  }

  @Transactional
  public boolean cancel(
      RequestId requestId
  ) {
    boolean result = requestRepository.delete(requestId);
    log.debug("request with id {} {}", requestId, result ? "canceled" : "failed");
    return result;
  }

  @Transactional
  public void createForAll(
      TimeSlot timeSlot
  ) {
    List<RequestableParkingSpotTemplate> templates = requestableSectionRepository.findAllTemplates();

    List<RequestableParkingSpot> groupedSections = templates.stream()
        .filter(template -> !requestableSectionRepository.intersects(template.parkingSpotId(), timeSlot))
        .map(template -> RequestableParkingSpot.createNew(
            template.parkingSpotId(),
            template.numberOfSections(),
            timeSlot)
        )
        .toList();
    log.debug("created time slots {} from {} for {}", groupedSections.size(), templates.size(), timeSlot);

    groupedSections.forEach(requestableSectionRepository::saveNew);
  }

  @Transactional
  public void makeValidFor(
      Instant date
  ) {
    List<Request> requests = requestRepository.findAllBy(date);
    log.debug("making valid {} requests", requests.size());

    eventPublisher.publish(new MadeRequestsValid(requests.stream()
        .map(request -> new MadeRequestsValid.Request(
            request.requestId(),
            request.requester().requesterId(),
            request.parkingSpotId(),
            request.timeSlot(),
            request.spotUnits()
        ))
        .toList()));

    requestRepository.deleteAll(requests.stream()
        .map(Request::requestId)
        .toList());
  }

}
