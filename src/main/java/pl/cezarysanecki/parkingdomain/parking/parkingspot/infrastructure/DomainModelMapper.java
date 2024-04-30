package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotInformation;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static ParkingSpot map(ParkingSpotEntity entity) {
        return new ParkingSpot(
                mapInformation(entity),
                entity.vehicles.stream()
                        .map(vehicle -> VehicleId.of(vehicle.vehicleId))
                        .collect(Collectors.toUnmodifiableSet()));
    }

    private static ParkingSpotInformation mapInformation(ParkingSpotEntity entity) {
        return ParkingSpotInformation.of(
                ParkingSpotId.of(entity.parkingSpotId),
                ParkingSpotOccupation.of(
                        entity.vehicles.stream().map(vehicle -> vehicle.size).reduce(0, Integer::sum),
                        entity.capacity
                ));
    }

}
