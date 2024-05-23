package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import io.vavr.collection.HashSet;
import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.commons.aggregates.Version;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.requests.ReservationRequest;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlot;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotEvent;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.timeslot.ReservationRequestsTimeSlotId;
import pl.cezarysanecki.parkingdomain.shared.occupation.ParkingSpotCapacity;
import pl.cezarysanecki.parkingdomain.shared.occupation.SpotUnits;
import pl.cezarysanecki.parkingdomain.shared.timeslot.TimeSlot;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
class ReservationRequestsTimeSlotEntity {

    UUID reservationRequestsTimeSlotId;
    int capacity;
    List<ReservationRequest> reservationRequests;
    int version;
    UUID templateId;
    TimeSlot timeSlot;

    ReservationRequestsTimeSlotEntity handle(ReservationRequestsTimeSlotEvent event) {
        return switch (event) {
            case ReservationRequestsTimeSlotEvent.ReservationRequestAppended appended -> {
                reservationRequests.add(appended.reservationRequest());
                yield this;
            }
            case ReservationRequestsTimeSlotEvent.ReservationRequestRemoved removed -> {
                reservationRequests.remove(removed.reservationRequest());
                yield this;
            }
            default -> this;
        };
    }

    int spaceLeft() {
        return capacity - reservationRequests.stream()
                .map(ReservationRequest::spotUnits)
                .map(SpotUnits::getValue)
                .reduce(0, Integer::sum);
    }

    ReservationRequestsTimeSlot toDomain() {
        return new ReservationRequestsTimeSlot(
                ReservationRequestsTimeSlotId.of(reservationRequestsTimeSlotId),
                HashSet.ofAll(reservationRequests.stream()),
                ParkingSpotCapacity.of(capacity),
                new Version(version));
    }

}
