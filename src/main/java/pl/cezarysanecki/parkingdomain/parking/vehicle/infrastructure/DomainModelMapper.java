package pl.cezarysanecki.parkingdomain.parking.vehicle.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.SpotUnits;
import pl.cezarysanecki.parkingdomain.parking.Vehicle;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleInformation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static Vehicle map(VehicleEntity entity) {
        return new Vehicle(
                VehicleInformation.of(
                        VehicleId.of(entity.vehicleId),
                        SpotUnits.of(entity.size)),
                entity.parkingSpotId.map(ParkingSpotId::of));
    }

}
