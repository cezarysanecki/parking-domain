package pl.cezarysanecki.parkingdomain.parking.infrastructure;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.NormalParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static NormalParkingSpot map(ParkingSpotEntity entity) {
        return new NormalParkingSpot(
                ParkingSpotId.of(entity.parkingSpotId),
                entity.capacity,
                entity.parkedVehicles.stream()
                        .map(vehicleEntity -> new Vehicle(
                                VehicleId.of(vehicleEntity.vehicleId),
                                VehicleSizeUnit.of(vehicleEntity.vehicleSizeUnit)))
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
