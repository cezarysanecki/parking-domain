package pl.cezarysanecki.parkingdomain.requestingreservation.view.parkingspot.infrastructure;

import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
class ParkingSpotReservationsViewEntity {

    UUID parkingSpotId;
    int capacity;
    Set<VehicleReservationEntity> currentReservations;

    int currentOccupation() {
        return currentReservations.stream()
                .map(vehicleReservation -> vehicleReservation.size)
                .reduce(0, Integer::sum);
    }

    @AllArgsConstructor
    static class VehicleReservationEntity {
        UUID reservationId;
        int size;
    }

}
