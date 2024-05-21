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
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestConfirmed;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requester.ReservationRequesterId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;

import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestCancelled;
import static pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents.ReservationRequestStored;

@Getter
@AllArgsConstructor
public class ParkingSpotReservationRequests {

    @NonNull
    private final ParkingSpotId parkingSpotId;
    @NonNull
    private final ParkingSpotTimeSlotId parkingSpotTimeSlotId;
    @NonNull
    private final ParkingSpotCapacity capacity;
    @NonNull
    private final Map<ReservationRequestId, ReservationRequest> reservationRequests;
    @NonNull
    private final Version version;

    public static ParkingSpotReservationRequests newOne(
            ParkingSpotId parkingSpotId,
            ParkingSpotTimeSlotId parkingSpotTimeSlotId,
            ParkingSpotCapacity capacity
    ) {
        return new ParkingSpotReservationRequests(
                parkingSpotId,
                parkingSpotTimeSlotId,
                capacity,
                HashMap.empty(),
                Version.zero());
    }

    public Try<ReservationRequestStored> storeRequest(ReservationRequesterId requesterId, SpotUnits spotUnits) {
        if (exceedsAllowedSpace(spotUnits)) {
            return Try.failure(new IllegalStateException("not enough space"));
        }
        return Try.of(() -> new ReservationRequestStored(
                parkingSpotId,
                parkingSpotTimeSlotId,
                ReservationRequest.newOne(requesterId, spotUnits)));
    }

    public Try<ReservationRequestCancelled> cancel(ReservationRequestId reservationRequestId) {
        Option<ReservationRequest> potentialReservationRequests = reservationRequests.get(reservationRequestId);
        if (potentialReservationRequests.isEmpty()) {
            return Try.failure(new IllegalArgumentException("no such reservation request"));
        }
        return Try.of(() -> new ReservationRequestCancelled(
                parkingSpotId,
                parkingSpotTimeSlotId,
                potentialReservationRequests.get()));
    }

    public Try<List<ReservationRequestConfirmed>> makeValid() {
        List<ValidReservationRequest> validReservationRequests = reservationRequests.values()
                .map(ValidReservationRequest::from)
                .toList();
        return Try.of(() -> validReservationRequests
                .map(validReservationRequest -> new ReservationRequestConfirmed(
                        parkingSpotId,
                        parkingSpotTimeSlotId,
                        validReservationRequest)));
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
