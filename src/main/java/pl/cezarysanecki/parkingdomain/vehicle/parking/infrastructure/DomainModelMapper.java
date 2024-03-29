package pl.cezarysanecki.parkingdomain.vehicle.parking.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleInformation;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleSize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static Vehicle map(VehicleEntity entity) {
        return new Vehicle(
                VehicleInformation.of(
                        VehicleId.of(entity.vehicleId),
                        VehicleSize.of(entity.size)),
                entity.parkingSpotId.map(ParkingSpotId::of));
    }

}
