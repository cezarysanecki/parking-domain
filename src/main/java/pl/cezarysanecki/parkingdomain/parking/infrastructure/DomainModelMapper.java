package pl.cezarysanecki.parkingdomain.parking.infrastructure;


import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.util.stream.Collectors;

class DomainModelMapper {

    static ParkingSpot map(ParkingSpotEntity entity) {
        return new ParkingSpot(
                ParkingSpotId.of(entity.parkingSpotId),
                entity.capacity,
                entity.parkedVehicles.stream()
                        .map(vehicleEntity -> new Vehicle(
                                VehicleId.of(vehicleEntity.vehicleId),
                                VehicleSizeUnit.of(vehicleEntity.vehicleSizeUnit)))
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
