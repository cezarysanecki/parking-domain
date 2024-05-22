package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.HashMap;
import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
class ReservationRequestsTimeSlotEntity {

    UUID parkingSpotId;
    UUID reservationRequestsTimeSlotId;
    Instant validSince;
    int capacity;
    List<ReservationRequest> reservationRequests;
    int version;

    static ReservationRequestsTimeSlotEntity from(ReservationRequestsTimeSlot reservationRequestsTimeSlot) {
        return new ReservationRequestsTimeSlotEntity(
                reservationRequestsTimeSlot.getParkingSpotId().getValue(),
                reservationRequestsTimeSlot.getReservationRequestsTimeSlotId().getValue(),
                reservationRequestsTimeSlot.getValidSince(),
                reservationRequestsTimeSlot.getCapacity().getValue(),
                reservationRequestsTimeSlot.getReservationRequests()
                        .values()
                        .toJavaList(),
                reservationRequestsTimeSlot.getVersion().getVersion());
    }

    ReservationRequestsTimeSlot toDomain() {
        return new ReservationRequestsTimeSlot(
                ParkingSpotId.of(parkingSpotId),
                ReservationRequestsTimeSlotId.of(reservationRequestsTimeSlotId),
                validSince,
                ParkingSpotCapacity.of(capacity),
                HashMap.ofAll(reservationRequests.stream(), ReservationRequest::getReservationRequestId, reservationRequest -> reservationRequest),
                new Version(version));
    }

}
