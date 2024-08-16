package pl.cezarysanecki.parkingdomain.management.client.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public interface ClientRegistered extends DomainEvent {

  ClientId clientId();

  record BusinessClient(
      ClientId clientId
  ) implements ClientRegistered {
  }

  record IndividualClient(
      ClientId clientId
  ) implements ClientRegistered {
  }

}
