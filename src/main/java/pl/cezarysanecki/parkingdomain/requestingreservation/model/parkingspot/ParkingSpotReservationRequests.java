package pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
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

@Getter
@AllArgsConstructor
public class ParkingSpotReservationRequests {

    @NonNull
    private final ParkingSpotId parkingSpotId;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private Map<ReservationRequestId, ReservationRequest> reservationRequests;
    @NonNull
    private final Version version;

    public static ParkingSpotReservationRequests newOne(ParkingSpotId parkingSpotId, ParkingSpotCapacity capacity) {
        return new ParkingSpotReservationRequests(
                parkingSpotId,
                capacity,
                HashMap.empty(),
                Version.zero());
    }

    public Try<ReservationRequest> storeRequest(ReservationRequesterId requesterId, SpotUnits spotUnits) {
        if (exceedsAllowedSpace(spotUnits)) {
            return Try.failure(new IllegalStateException("not enough space"));
        }

        ReservationRequest reservationRequest = ReservationRequest.newOne(requesterId, spotUnits);
        reservationRequests = reservationRequests.put(reservationRequest.getReservationRequestId(), reservationRequest);

        return Try.of(() -> reservationRequest);
    }

    public Try<ReservationRequest> cancel(ReservationRequestId reservationRequestId) {
        Option<ReservationRequest> potentialReservationRequests = reservationRequests.get(reservationRequestId);
        if (potentialReservationRequests.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such reservation request"));
        }
        ReservationRequest reservationRequest = potentialReservationRequests.get();

        reservationRequests = reservationRequests.remove(reservationRequestId);

        return Try.of(() -> reservationRequest);
    }

    public List<ValidReservationRequest> makeValid() {
        List<ValidReservationRequest> validReservationRequests = reservationRequests.values()
                .map(ValidReservationRequest::from)
                .toList();
        reservationRequests = HashMap.empty();
        return validReservationRequests;
    }

    public int spaceLeft() {
        return capacity.getValue() - currentOccupation();
    }

    public boolean isFree() {
        return reservationRequests.isEmpty();
    }

    private int currentOccupation() {
        return reservationRequests.values()
                .map(ReservationRequest::getSpotUnits)
                .map(SpotUnits::getValue)
                .reduceOption(Integer::sum)
                .getOrElse(0);
    }

    private boolean exceedsAllowedSpace(SpotUnits spotUnits) {
        return spotUnits.getValue() + currentOccupation() > capacity.getValue();
    }

}
