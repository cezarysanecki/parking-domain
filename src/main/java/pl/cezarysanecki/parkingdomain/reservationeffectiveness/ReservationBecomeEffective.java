package pl.cezarysanecki.parkingdomain.reservationeffectiveness;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.commons.events.DomainEvent;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

@Value
public class ReservationBecomeEffective implements DomainEvent {

    @NonNull ReservationId reservationId;

}
