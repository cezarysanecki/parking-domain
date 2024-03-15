package pl.cezarysanecki.parkingdomain.parking.infrastructure;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpot map(ParkingSpotEntity entity) {
        Set<Vehicle> parkedVehicles = entity.parkedVehicles.stream()
                .map(vehicleEntity -> new Vehicle(
                        VehicleId.of(vehicleEntity.vehicleId),
                        VehicleSizeUnit.of(vehicleEntity.vehicleSizeUnit)))
                .collect(Collectors.toUnmodifiableSet());

        return entity.reservation
                .map(clientId -> new ParkingSpot(
                        ParkingSpotId.of(entity.parkingSpotId),
                        entity.capacity,
                        parkedVehicles,
                        entity.outOfOrder,
                        clientId))
                .getOrElse(() -> new ParkingSpot(
                        ParkingSpotId.of(entity.parkingSpotId),
                        entity.capacity,
                        parkedVehicles,
                        entity.outOfOrder));
    }

}
