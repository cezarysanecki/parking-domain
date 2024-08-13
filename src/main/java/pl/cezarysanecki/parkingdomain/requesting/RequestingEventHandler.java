package pl.cezarysanecki.parkingdomain.requesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientRegistered;
import pl.cezarysanecki.parkingdomain.management.parkingspot.api.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

@Slf4j
@RequiredArgsConstructor
class RequestingEventHandler {

  private final RequesterRepository requesterRepository;
  private final RequestableSectionRepository requestableSectionRepository;

  @EventListener
  public void handle(ClientRegistered.IndividualClient event) {
    RequesterId requesterId = new RequesterId(event.clientId().value());

    log.debug("saving requester with lower limit with id {}", requesterId);
    requesterRepository.saveNew(requesterId, 1);
  }


  @EventListener
  public void handle(ClientRegistered.BusinessClient event) {
    RequesterId requesterId = new RequesterId(event.clientId().value());

    log.debug("saving requester with higher limit with id {}", requesterId);
    requesterRepository.saveNew(requesterId, 20);
  }

  @EventListener
  public void handle(ParkingSpotAdded event) {
    log.debug("storing parking spot as reservation requests template with id {}", event.parkingSpotId());
    requestableSectionRepository.saveTemplate(event.parkingSpotId(), event.sections());
  }

}
