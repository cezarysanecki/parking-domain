package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.management.parkingspot.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.SpotOccupation;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpot map(ParkingSpotEntity entity) {
        return new ParkingSpot(
                ParkingSpotId.of(entity.parkingSpotId),
                SpotOccupation.of(
                        entity.vehicles.stream().map(vehicle -> vehicle.size).reduce(0, Integer::sum),
                        entity.capacity),
                entity.vehicles.stream()
                        .map(vehicle -> VehicleId.of(vehicle.vehicleId))
                        .collect(Collectors.toUnmodifiableSet()));
    }

}
