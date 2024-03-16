package pl.cezarysanecki.parkingdomain.parking.infrastructure;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.cezarysanecki.parkingdomain.clientreservations.model.ClientId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotReservation;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservationschedule.model.ReservationId;

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
                .map(reservation -> new ParkingSpot(
                        ParkingSpotId.of(entity.parkingSpotId),
                        entity.capacity,
                        parkedVehicles,
                        entity.outOfOrder,
                        new ParkingSpotReservation(
                                ClientId.of(reservation.clientId),
                                ReservationId.of(reservation.reservationId)
                        )))
                .getOrElse(() -> new ParkingSpot(
                        ParkingSpotId.of(entity.parkingSpotId),
                        entity.capacity,
                        parkedVehicles,
                        entity.outOfOrder));
    }

}
