package pl.cezarysanecki.parkingdomain.parking.parkingspot.model;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleSize;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parking.parkingspot.model.ParkingSpotEvent.ParkingSpotOccupiedEvents.events;

@Value
public class OpenParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotInformation parkingSpotInformation;

    public Either<ParkingSpotEvent.ParkingSpotOccupationFailed, ParkingSpotEvent.ParkingSpotOccupiedEvents> occupy(VehicleId vehicleId, VehicleSize vehicleSize) {
        ParkingSpotOccupation parkingSpotOccupation = getParkingSpotOccupation();
        if (!parkingSpotOccupation.canHandle(vehicleSize)) {
            return announceFailure(new ParkingSpotEvent.ParkingSpotOccupationFailed(getParkingSpotId(), vehicleId, "there is not enough space for vehicle"));
        }

        ParkingSpotEvent.ParkingSpotOccupied parkingSpotOccupied = new ParkingSpotEvent.ParkingSpotOccupied(getParkingSpotId(), vehicleId, vehicleSize);
        if (parkingSpotOccupation.occupyWith(vehicleSize).isFull()) {
            return announceSuccess(ParkingSpotEvent.ParkingSpotOccupiedEvents.events(getParkingSpotId(), parkingSpotOccupied, new ParkingSpotEvent.FullyOccupied(getParkingSpotId())));
        }
        return announceSuccess(ParkingSpotEvent.ParkingSpotOccupiedEvents.events(getParkingSpotId(), parkingSpotOccupied));
    }

}
