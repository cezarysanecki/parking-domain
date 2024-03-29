package pl.cezarysanecki.parkingdomain.parking.parkingspot.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.OccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.OpenParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotInformation;
import pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DomainModelMapper {

    static OpenParkingSpot mapOpen(ParkingSpotEntity entity) {
        return new OpenParkingSpot(mapInformation(entity));
    }

    static OccupiedParkingSpot mapOccupied(ParkingSpotEntity entity) {
        return new OccupiedParkingSpot(
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
