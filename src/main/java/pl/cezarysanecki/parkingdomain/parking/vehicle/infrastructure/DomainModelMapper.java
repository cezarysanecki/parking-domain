package pl.cezarysanecki.parkingdomain.parking.vehicle.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.catalogue.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.Vehicle;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleSize;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleInformation;

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
