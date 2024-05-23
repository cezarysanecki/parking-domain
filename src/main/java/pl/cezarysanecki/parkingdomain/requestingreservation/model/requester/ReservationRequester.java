package pl.cezarysanecki.parkingdomain.requestingreservation.model.requester;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent.ReservationRequestAppended;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterEvent.ReservationRequestRemoved;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequestId;

@Getter
@AllArgsConstructor
public class ReservationRequester {

    @NonNull
    private final ReservationRequesterId requesterId;
    @NonNull
    private final Set<ReservationRequestId> currentRequests;
    private final int limit;
    @NonNull
    private final Version version;

    public static ReservationRequester newOne(ReservationRequesterId requesterId, int limit) {
        return new ReservationRequester(requesterId, HashSet.empty(), limit, Version.zero());
    }

    public Try<ReservationRequestAppended> append(ReservationRequestId reservationRequestId) {
        if (willBeTooManyRequests(reservationRequestId)) {
            return Try.failure(new IllegalStateException("too many reservation requests"));
        }
        return Try.of(() -> new ReservationRequestAppended(requesterId, reservationRequestId, version));
    }

    public Try<ReservationRequestRemoved> remove(ReservationRequestId reservationRequestId) {
        if (!currentRequests.contains(reservationRequestId)) {
            return Try.failure(new IllegalStateException("does not have request"));
        }
        return Try.of(() -> new ReservationRequestRemoved(requesterId, reservationRequestId, version));
    }

    private boolean willBeTooManyRequests(ReservationRequestId reservationRequestId) {
        return currentRequests.size() + 1 > limit;
    }

}
