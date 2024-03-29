package pl.cezarysanecki.parkingdomain.parkingspot.parking.infrastructure;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
class ParkingSpotVehicleEntity {

    final UUID vehicleId;
    final int size;

}
