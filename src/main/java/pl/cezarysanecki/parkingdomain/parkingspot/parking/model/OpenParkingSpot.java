package pl.cezarysanecki.parkingdomain.parkingspot.parking.model;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupiedEvents;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleId;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleSize;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.FullyOccupied;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupationFailed;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupied;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupiedEvents.events;

@Value
public class OpenParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotInformation parkingSpotInformation;

    public Either<ParkingSpotOccupationFailed, ParkingSpotOccupiedEvents> occupy(VehicleId vehicleId, VehicleSize vehicleSize) {
        ParkingSpotOccupation parkingSpotOccupation = getParkingSpotOccupation();
        if (!parkingSpotOccupation.canHandle(vehicleSize)) {
            return announceFailure(new ParkingSpotOccupationFailed(getParkingSpotId(), vehicleId, "there is not enough space for vehicle"));
        }

        ParkingSpotOccupied parkingSpotOccupied = new ParkingSpotOccupied(getParkingSpotId(), vehicleId, vehicleSize);
        if (parkingSpotOccupation.occupyWith(vehicleSize).isFull()) {
            return announceSuccess(events(getParkingSpotId(), parkingSpotOccupied, new FullyOccupied(getParkingSpotId())));
        }
        return announceSuccess(events(getParkingSpotId(), parkingSpotOccupied));
    }

}
