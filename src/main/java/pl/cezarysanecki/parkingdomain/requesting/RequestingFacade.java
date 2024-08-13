package pl.cezarysanecki.parkingdomain.requesting;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.MadeRequestsValid;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class RequestingFacade {

  private final RequestableSectionRepository requestableSectionRepository;
  private final RequesterRepository requesterRepository;
  private final RequestRepository requestRepository;
  private final EventPublisher eventPublisher;

  @Transactional
  public int createForAll(
      TimeSlot timeSlot
  ) {
    List<RequestableParkingSpotTemplate> templates = requestableSectionRepository.findAllTemplates();

    List<RequestableSectionsGrouped> groupedSections = templates.stream()
        .filter(template -> !requestableSectionRepository.intersects(template.parkingSpotId(), timeSlot))
        .map(template -> RequestableSectionsGrouped.createNew(
            template.parkingSpotId(),
            template.sections(),
            timeSlot)
        )
        .toList();

    groupedSections.forEach(requestableSectionRepository::saveNew);

    return groupedSections.size();
  }

  @Transactional
  public boolean request(
      RequesterId requesterId,
      ParkingSpotId parkingSpotId,
      TimeSlot timeSlot,
      SpotUnits spotUnits
  ) {
    RequestableSectionsGrouped requestableSectionsGrouped = requestableSectionRepository.findFreeSectionsFor(
        parkingSpotId, timeSlot, spotUnits);
    Requester requester = requesterRepository.findBy(requesterId);

    RequestId requestId = RequestId.newOne();
    if (!requestableSectionsGrouped.requestBy(requestId) || !requester.append(requestId)) {
      return false;
    }
    requestRepository.saveCheckingVersion(new Request(
        requestId, requester, parkingSpotId, timeSlot, requestableSectionsGrouped.sections()
    ));
    return true;
  }

  @Transactional
  public boolean requestWhole(
      RequesterId requesterId,
      ParkingSpotId parkingSpotId,
      TimeSlot timeSlot
  ) {
    RequestableSectionsGrouped requestableSectionsGrouped = requestableSectionRepository.loadBy(
        parkingSpotId, timeSlot);
    Requester requester = requesterRepository.findBy(requesterId);

    RequestId requestId = RequestId.newOne();
    if (!requestableSectionsGrouped.requestBy(requestId) || !requester.append(requestId)) {
      return false;
    }
    requestRepository.saveCheckingVersion(new Request(
        requestId, requester, parkingSpotId, timeSlot, requestableSectionsGrouped.sections()
    ));
    return true;
  }

  @Transactional
  public boolean cancel(
      RequestId requestId
  ) {
    return requestRepository.delete(requestId);
  }

  @Transactional
  public void makeValidFor(
      Instant date
  ) {
    List<Request> requests = requestRepository.findAllBy(date);

    eventPublisher.publish(new MadeRequestsValid(requests.stream()
        .map(request -> new MadeRequestsValid.Request(
            request.requestId(),
            request.requester().requesterId(),
            request.parkingSpotId(),
            request.timeSlot(),
            request.sections().stream().map(RequestableSection::sectionId).toList()
        ))
        .toList()));

    requestRepository.deleteAll(requests.stream()
        .map(Request::requestId)
        .toList());
  }

}
