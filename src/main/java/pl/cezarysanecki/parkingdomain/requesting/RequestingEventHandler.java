package pl.cezarysanecki.parkingdomain.requesting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import pl.cezarysanecki.parkingdomain.management.client.ClientRegistered;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotAdded;
import pl.cezarysanecki.parkingdomain.requesting.api.RequesterId;

@Slf4j
class RequestingEventHandler {

  @EventListener
  public void handle(ClientRegistered.IndividualClientRegistered event) {
    RequesterId requesterId = RequesterId.of(event.clientId().getValue());

    log.debug("saving requester with lower limit with id: {}", requesterId);
    reservationRequesterRepository.saveNew(requesterId, 1);
  }


  @EventListener
  public void handle(ClientRegistered.BusinessClientRegistered event) {
    RequesterId requesterId = RequesterId.of(event.clientId().getValue());

    log.debug("saving requester with higher limit with id: {}", requesterId);
    reservationRequesterRepository.saveNew(requesterId, 20);
  }

  @EventListener
  public void handle(ParkingSpotAdded event) {
    ReservationRequestsTemplate template = new ReservationRequestsTemplate(
        ReservationRequestsTemplateId.newOne(),
        event.parkingSpotId(),
        event.category(),
        event.capacity());

    log.debug("storing parking spot as reservation requests template with id {}", template.parkingSpotId());
    repository.save(template);
  }

}
