package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ReservationRequestsTimeSlot {

    @NonNull
    private final ParkingSpotId parkingSpotId;
    @NonNull
    private final ReservationRequestsTimeSlotId reservationRequestsTimeSlotId;
    @NonNull
    private final Instant validSince;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private final Map<ReservationRequestId, ReservationRequest> reservationRequests;
    @NonNull
    private final Version version;

    public static ReservationRequestsTimeSlot newOne(
            ParkingSpotId parkingSpotId,
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            Instant validSince,
            ParkingSpotCapacity capacity
    ) {
        return new ReservationRequestsTimeSlot(
                parkingSpotId,
                reservationRequestsTimeSlotId,
                validSince,
                capacity,
                HashMap.empty(),
                Version.zero());
    }

    public Try<ReservationRequest> append(ReservationRequesterId requesterId, SpotUnits spotUnits) {
        if (exceedsAllowedSpace(spotUnits)) {
            return Try.failure(new IllegalStateException("not enough space"));
        }
        return Try.of(() -> ReservationRequest.newOne(requesterId, spotUnits));
    }

    public Try<ReservationRequest> remove(ReservationRequestId reservationRequestId) {
        Option<ReservationRequest> potentialReservationRequests = reservationRequests.get(reservationRequestId);
        if (potentialReservationRequests.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such reservation request"));
        }
        return Try.of(potentialReservationRequests::get);
    }

    private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
        return spotUnits.getValue() + currentRequestedOccupation() > capacity.getValue();
    }

    private int currentRequestedOccupation() {
        return reservationRequests.values()
                .map(ReservationRequest::getSpotUnits)
                .map(SpotUnits::getValue)
                .reduceOption(Integer::sum)
                .getOrElse(0);
    }

}
