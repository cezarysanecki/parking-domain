package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.shared.reservationrequest.ReservationRequestId;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent.ReservationRequestAppended;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent.ReservationRequestRemoved;

@Getter
@AllArgsConstructor
public class ReservationRequester {

    @NonNull
    private final ReservationRequesterId requesterId;
    @NonNull
    private final Set<ReservationRequestId> reservationRequests;
    private int limit;
    @NonNull
    private final Version version;

    public static ReservationRequester newOne(ReservationRequesterId requesterId, int limit) {
        return new ReservationRequester(requesterId, HashSet.empty(), limit, Version.zero());
    }

    public Try<ReservationRequestAppended> append(ReservationRequestId reservationRequestId) {
        if (willBeTooManyRequests(reservationRequestId)) {
            return Try.failure(new IllegalStateException("too many reservation requests"));
        }
        return Try.of(() -> new ReservationRequestAppended(requesterId, reservationRequestId));
    }

    public Try<ReservationRequestRemoved> remove(ReservationRequestId reservationRequestId) {
        if (!reservationRequests.contains(reservationRequestId)) {
            return Try.failure(new IllegalStateException("reservation request not found"));
        }
        return Try.of(() -> new ReservationRequestRemoved(requesterId, reservationRequestId));
    }

    private boolean willBeTooManyRequests(ReservationRequestId reservationRequestId) {
        return reservationRequests.size() + 1 > limit;
    }

}
