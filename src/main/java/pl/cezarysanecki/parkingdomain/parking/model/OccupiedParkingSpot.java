package pl.cezarysanecki.parkingdomain.parking.model;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.CompletelyFreedUp;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.ReleasingFailed;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeft;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotEvent.VehicleLeftEvents;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class OccupiedParkingSpot implements ParkingSpot {

    @Getter
    private final ParkingSpotBase parkingSpot;

    public Either<ReleasingFailed, VehicleLeftEvents> releaseBy(VehicleId vehicleId) {
        ParkingSpotId parkingSpotId = parkingSpot.getParkingSpotId();
        ParkedVehicles parkedVehicles = parkingSpot.getParkedVehicles();

        Option<Vehicle> parkedVehicle = parkedVehicles.findBy(vehicleId);
        if (parkedVehicle.isEmpty()) {
            return announceFailure(new ReleasingFailed(parkingSpotId, List.of(vehicleId), "vehicle not park on this spot"));
        }
        Vehicle vehicle = parkedVehicle.get();

        VehicleLeft vehicleLeft = new VehicleLeft(parkingSpotId, vehicle);
        if (isCompletelyFreedUp(vehicle.getVehicleSizeUnit())) {
            return announceSuccess(VehicleLeftEvents.events(
                    parkingSpotId, List.of(vehicleLeft), new CompletelyFreedUp(parkingSpotId)));
        }
        return announceSuccess(VehicleLeftEvents.events(parkingSpotId, List.of(vehicleLeft)));
    }

    public Either<ReleasingFailed, VehicleLeftEvents> releaseAll() {
        ParkingSpotId parkingSpotId = parkingSpot.getParkingSpotId();
        ParkedVehicles parkedVehicles = parkingSpot.getParkedVehicles();

        if (parkedVehicles.isEmpty()) {
            return announceFailure(new ReleasingFailed(parkingSpotId, List.empty(), "parking spot is empty"));
        }

        List<VehicleLeft> vehiclesLeft = List.ofAll(parkedVehicles.getCollection().stream()
                .map(parkedVehicle -> new VehicleLeft(parkingSpotId, parkedVehicle))
                .toList());
        return announceSuccess(VehicleLeftEvents.events(parkingSpotId, vehiclesLeft, new CompletelyFreedUp(parkingSpotId)));
    }

    private boolean isCompletelyFreedUp(VehicleSizeUnit vehicleSizeUnit) {
        return parkingSpot.getParkedVehicles().occupation() - vehicleSizeUnit.getValue() == 0;
    }

}
