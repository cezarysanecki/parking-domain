package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
class ParkingSpotVehicleEntity {

    final UUID vehicleId;
    final int size;

}
