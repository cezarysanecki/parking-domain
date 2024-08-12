package pl.cezarysanecki.parkingdomain.requesting.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.ClientRegistered;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.requesting.RequestableSectionRepository;
import pl.cezarysanecki.parkingdomain.requesting.RequesterRepository;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

@Slf4j
@RequiredArgsConstructor
class RequestingEventHandler {

  private final RequesterRepository requesterRepository;
  private final RequestableSectionRepository requestableSectionRepository;

  @EventListener
  public void handle(ClientRegistered.IndividualClientRegistered event) {
    RequesterId requesterId = RequesterId.of(event.clientId().getValue());

    log.debug("saving requester with lower limit with id: {}", requesterId);
    requesterRepository.saveNew(requesterId, 1);
  }


  @EventListener
  public void handle(ClientRegistered.BusinessClientRegistered event) {
    RequesterId requesterId = RequesterId.of(event.clientId().getValue());

    log.debug("saving requester with higher limit with id: {}", requesterId);
    requesterRepository.saveNew(requesterId, 20);
  }

  @EventListener
  public void handle(ParkingSpotAdded event) {
    log.debug("storing parking spot as reservation requests template with id {}", event.parkingSpotId());
    requestableSectionRepository.saveTemplate(event.parkingSpotId(), event.sections());
  }

}
