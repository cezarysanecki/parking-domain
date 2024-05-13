package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ReservationRequestId;

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

    public Try<ReservationRequest> append(ReservationRequest reservationRequest) {
        if (willBeTooManyRequests(reservationRequest)) {
            return Try.failure(new IllegalStateException("too many reservation requests"));
        }

        reservationRequests = reservationRequests.add(reservationRequest.getReservationRequestId());

        return Try.of(() -> reservationRequest);
    }

    public Try<ReservationRequest> remove(ReservationRequest reservationRequest) {
        if (!reservationRequests.contains(reservationRequest.getReservationRequestId())) {
            return Try.failure(new IllegalStateException("reservation request not found"));
        }

        reservationRequests = reservationRequests.remove(reservationRequest.getReservationRequestId());

        return Try.of(() -> reservationRequest);
    }

    public boolean isEmpty() {
        return reservationRequests.isEmpty();
    }

    private boolean willBeTooManyRequests(ReservationRequest reservationRequest) {
        return reservationRequests.size() + 1 > limit;
    }

}
