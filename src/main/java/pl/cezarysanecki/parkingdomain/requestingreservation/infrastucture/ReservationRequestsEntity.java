package pl.cezarysanecki.parkingdomain.requestingreservation.infrastucture;

import lombok.AllArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotCategory;
import pl.cezarysanecki.parkingdomain.requestingreservation.model.parkingspot.ParkingSpotReservationRequestsEvents;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
class ReservationRequestsEntity {

    UUID parkingSpotId;
    UUID parkingSpotTimeSlotId;
    ParkingSpotCategory category;
    int capacity;
    List<CurrentRequestEntity> currentRequests;
    Instant from;
    Instant to;
    int version;

    ReservationRequestsEntity handle(ParkingSpotReservationRequestsEvents event) {
        return switch (event) {
            case ParkingSpotReservationRequestsEvents.ReservationRequestStored stored -> {
                this.currentRequests
                        .add(new CurrentRequestEntity(
                                stored.reservationRequest().getReservationRequestId().getValue(),
                                stored.reservationRequest().getReservationRequesterId().getValue(),
                                stored.reservationRequest().getSpotUnits().getValue()
                        ));
                yield this;
            }
            case ParkingSpotReservationRequestsEvents.ReservationRequestCancelled cancelled -> {
                this.currentRequests
                        .removeIf(currentRequest -> currentRequest.reservationRequestId.equals(
                                cancelled.reservationRequest().getReservationRequestId().getValue()));
                yield this;
            }
            case ParkingSpotReservationRequestsEvents.ReservationRequestConfirmed confirmed -> {
                this.currentRequests
                        .removeIf(currentRequest -> currentRequest.reservationRequestId.equals(
                                confirmed.validReservationRequest().getReservationRequestId().getValue()));
                yield this;
            }
            default -> this;
        };
    }

    int spaceLeft() {
        return capacity - currentRequests.stream().map(currentRequest -> currentRequest.units).reduce(0, Integer::sum);
    }

    @AllArgsConstructor
    static class CurrentRequestEntity {

        UUID reservationRequestId;
        UUID requesterId;
        int units;

    }

}
