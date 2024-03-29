package pl.cezarysanecki.parkingdomain.parkingspot.parking.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.OccupiedParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.OpenParkingSpot;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotInformation;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotOccupation;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;

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
