package pl.cezarysanecki.parkingdomain.reservation.effectiveness;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor
class ReservationToMakeEffectiveEntity {

    @NonNull UUID reservationId;
    @NonNull UUID parkingSpotId;

}
