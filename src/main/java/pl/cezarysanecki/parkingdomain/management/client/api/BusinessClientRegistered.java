package pl.cezarysanecki.parkingdomain.management.client.api;

import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;

public record BusinessClientRegistered(
    ClientId clientId
) implements DomainEvent {
}
