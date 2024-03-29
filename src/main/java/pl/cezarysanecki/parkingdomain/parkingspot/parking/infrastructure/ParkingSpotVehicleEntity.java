package pl.cezarysanecki.parkingdomain.parkingspot.parking.infrastructure;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
class ParkingSpotVehicleEntity {

    final UUID vehicleId;
    final int size;

}
