package pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent.ReservationRequestAppended;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent.ReservationRequestRemoved;

@Getter
@AllArgsConstructor
public class ReservationRequestsTimeSlot {

    @NonNull
    private final ReservationRequestsTimeSlotId timeSlotId;
    @NonNull
    private final Set<ReservationRequest> reservationRequests;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private final Version version;

    public static ReservationRequestsTimeSlot newOne(
            ReservationRequestsTimeSlotId reservationRequestsTimeSlotId,
            ParkingSpotCapacity capacity
    ) {
        return new ReservationRequestsTimeSlot(
                reservationRequestsTimeSlotId,
                HashSet.empty(),
                capacity,
                Version.zero());
    }

    public Try<ReservationRequestAppended> append(ReservationRequest reservationRequest) {
        if (exceedsAllowedSpace(reservationRequest.spotUnits())) {
            return Try.failure(new IllegalStateException("not enough space"));
        }
        return Try.of(() -> new ReservationRequestAppended(timeSlotId, reservationRequest, version));
    }

    public Try<ReservationRequestRemoved> remove(ReservationRequestId reservationRequestId) {
        Option<ReservationRequest> request = findRequestBy(reservationRequestId);
        if (request.isEmpty()) {
            return Try.failure(new IllegalStateException("not enough space"));
        }
        return Try.of(() -> new ReservationRequestRemoved(timeSlotId, request.get(), version));
    }

    private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
        return spotUnits.getValue() + currentRequestedOccupation() > capacity.getValue();
    }

    private int currentRequestedOccupation() {
        return reservationRequests
                .map(ReservationRequest::spotUnits)
                .map(SpotUnits::getValue)
                .reduceOption(Integer::sum)
                .getOrElse(0);
    }

    private Option<ReservationRequest> findRequestBy(ReservationRequestId reservationRequestId) {
        return reservationRequests
                .find(reservationRequest -> reservationRequest.reservationRequestId().equals(reservationRequestId));
    }

}
