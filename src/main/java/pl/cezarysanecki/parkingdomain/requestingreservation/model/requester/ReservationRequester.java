package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestId;

@Getter
@AllArgsConstructor
public class ReservationRequester {

    @NonNull
    private final ReservationRequesterId requesterId;
    @NonNull
    private Set<ReservationRequestId> reservationRequests;
    private int limit;

    public static ReservationRequester ofLimit(ReservationRequesterId requesterId, int limit) {
        return new ReservationRequester(requesterId, HashSet.empty(), limit);
    }

    public Try<ReservationRequestId> append(ReservationRequestId reservationRequestId) {
        if (willBeTooManyRequests(reservationRequestId)) {
            return Try.failure(new IllegalStateException("too many reservation requests"));
        }

        reservationRequests = reservationRequests.add(reservationRequestId);

        return Try.of(() -> reservationRequestId);
    }

    public Try<ReservationRequestId> remove(ReservationRequestId reservationRequestId) {
        if (!reservationRequests.contains(reservationRequestId)) {
            return Try.failure(new IllegalStateException("reservation request not found"));
        }

        reservationRequests = reservationRequests.remove(reservationRequestId);

        return Try.of(() -> reservationRequestId);
    }

    public boolean isEmpty() {
        return reservationRequests.isEmpty();
    }

    private boolean willBeTooManyRequests(ReservationRequestId reservationRequestId) {
        return reservationRequests.size() + 1 > limit;
    }

}
