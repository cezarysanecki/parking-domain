package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequest;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent.ReservationRequestAppended;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent.ReservationRequestRemoved;

@Getter
@AllArgsConstructor
public class ReservationRequestsTimeSlot {

    @NonNull
    private final ReservationRequestsTimeSlotId reservationRequestsTimeSlotId;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private final Map<ReservationRequestId, ReservationRequest> reservationRequests;
    @NonNull
    private final Version version;

    public static ReservationRequestsTimeSlot newOne(
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            ParkingSpotCapacity capacity
    ) {
        return new ReservationRequestsTimeSlot(
                reservationRequestsTimeSlotId,
                capacity,
                HashMap.empty(),
                Version.zero());
    }

    public Try<ReservationRequestAppended> append(ReservationRequest reservationRequest) {
        if (exceedsAllowedSpace(reservationRequest.getSpotUnits())) {
            return Try.failure(new IllegalStateException("not enough space"));
        }
        return Try.of(() -> new ReservationRequestAppended(reservationRequestsTimeSlotId, reservationRequest));
    }

    public Try<ReservationRequestRemoved> remove(ReservationRequestId reservationRequestId) {
        Option<ReservationRequest> potentialReservationRequests = reservationRequests.get(reservationRequestId);
        if (potentialReservationRequests.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such reservation request"));
        }
        return Try.of(() -> new ReservationRequestRemoved(reservationRequestsTimeSlotId, potentialReservationRequests.get()));
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
