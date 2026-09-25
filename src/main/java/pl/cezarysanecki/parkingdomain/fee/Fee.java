package pl.cezarysanecki.parkingdomain.fee;

import pl.cezarysanecki.parkingdomain.fee.api.FeeId;
import pl.cezarysanecki.parkingdomain.fee.api.FeeType;
import pl.cezarysanecki.parkingdomain.fee.api.Money;
import pl.cezarysanecki.parkingdomain.management.client.api.ClientId;
import pl.cezarysanecki.parkingdomain.reservation.api.ReservationId;

import java.time.Instant;

record Fee(
    FeeId feeId,
    ClientId clientId,
    ReservationId reservationId,
    FeeType type,
    Money amount,
    Instant chargedAt
) {
}
