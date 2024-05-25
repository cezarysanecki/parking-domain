package pl.cezarysanecki.parkingdomain.management.client;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public interface ClientRegistered extends DomainEvent {

  ClientId clientId();

  record IndividualClientRegistered(
      ClientId clientId
  ) implements ClientRegistered {
  }

  record BusinessClientRegistered(
      ClientId clientId
  ) implements ClientRegistered {
  }

}
