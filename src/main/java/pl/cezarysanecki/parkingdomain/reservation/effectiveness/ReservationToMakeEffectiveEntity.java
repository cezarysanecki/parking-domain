package pl.cezarysanecki.parkingdomain.reservation.effectiveness;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
class ReservationToMakeEffectiveEntity {

    @NonNull UUID reservationId;
    @NonNull UUID parkingSpotId;
    @NonNull LocalDateTime validSince;

}
