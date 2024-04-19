package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.infrastructure;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ParkingSpotReservationRequestsViewEntity {

    UUID parkingSpotId;
    int capacity;
    Set<VehicleReservationRequestEntity> currentReservations;

    int currentOccupation() {
        return currentReservations.stream()
                .map(vehicleReservationRequest -> vehicleReservationRequest.size)
                .reduce(0, Integer::sum);
    }

    @AllArgsConstructor
    static class VehicleReservationRequestEntity {
        UUID reservationId;
        int size;
    }

}
