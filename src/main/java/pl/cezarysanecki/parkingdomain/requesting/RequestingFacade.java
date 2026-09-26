package pl.cezarysanecki.parkingdomain.requesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.commons.events.EventPublisher;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requesting.api.MadeRequestsValid;
import pl.cezarysanecki.parkingdomain.requesting.api.RequestId;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;
import pl.cezarysanecki.parkingdomain.shared.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.TimeSlot;
import pl.cezarysanecki.parkingdomain.shared.VehicleType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RequestingFacade {

  private final RequestableParkingSpotRepository requestableParkingSpotRepository;
  private final RequesterRepository requesterRepository;
  private final RequestRepository requestRepository;
  private final EventPublisher eventPublisher;

  @Transactional
  public Optional<RequestId> request(
      RequesterId requesterId,
      ParkingSpotId parkingSpotId,
      TimeSlot timeSlot,
      VehicleType vehicleType
  ) {
    SpotUnits spotUnits = vehicleType.spotUnits();
    log.debug("requesting parking spot with id {} by {} ({} units) for {} time slot", parkingSpotId, vehicleType, spotUnits, timeSlot);
    RequestableParkingSpot requestableParkingSpot = requestableParkingSpotRepository.findFor(parkingSpotId, timeSlot);
    Requester requester = requesterRepository.findBy(requesterId);

    RequestId requestId = RequestId.newOne();
    if (!requestableParkingSpot.canRequestFor(spotUnits) || !requester.canAppend(requestId)) {
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
    List<RequestableParkingSpotTemplate> templates = requestableParkingSpotRepository.findAllTemplates();

    List<RequestableParkingSpot> requestableParkingSpots = templates.stream()
        .filter(template -> !requestableParkingSpotRepository.intersects(template.parkingSpotId(), timeSlot))
        .map(template -> RequestableParkingSpot.createNew(
            template.parkingSpotId(),
            template.numberOfSections(),
            timeSlot)
        )
        .toList();
    log.debug("created time slots [{}/{}] for {}", requestableParkingSpots.size(), templates.size(), timeSlot);

    requestableParkingSpotRepository.saveAllNewFor(requestableParkingSpots);
  }

  @Transactional
  public void makeValidFor(
      LocalDate day
  ) {
    List<RequestForReservation> requests = requestRepository.findAllBy(day);
    log.debug("making valid {} requests", requests.size());

    eventPublisher.publish(new MadeRequestsValid(requests.stream()
        .map(request -> new MadeRequestsValid.Request(
            request.requestId(),
            request.requesterId(),
            request.parkingSpotId(),
            request.timeSlot(),
            request.spotUnits()
        ))
        .toList()));

    requestRepository.deleteAll(requests.stream()
        .map(RequestForReservation::requestId)
        .toList());
    requestableParkingSpotRepository.deleteAllFor(day);
  }

}
