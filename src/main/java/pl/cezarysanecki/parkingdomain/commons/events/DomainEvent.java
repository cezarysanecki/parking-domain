package pl.cezarysanecki.parkingdomain.commons.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    default UUID getEventId() {
        return UUID.randomUUID();
    }

    default Instant getWhen() {
        return Instant.now();
    }

}
