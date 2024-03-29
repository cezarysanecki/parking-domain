package pl.cezarysanecki.parkingdomain.parkingspot.parking.model;

import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.vehicle.model.Vehicle;

import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceFailure;
import static pl.cezarysanecki.parkingdomain.commons.events.EitherResult.announceSuccess;
import static pl.cezarysanecki.parkingdomain.parkingspot.parking.model.ParkingSpotEvent.ParkingSpotOccupiedEvents.events;

@Value
public class OpenParkingSpot implements ParkingSpot {

    @NonNull
    ParkingSpotInformation parkingSpotInformation;

    public Either<ParkingSpotEvent.ParkingSpotOccupationFailed, ParkingSpotEvent.ParkingSpotOccupiedEvents> occupy(Vehicle vehicle) {
        ParkingSpotOccupation parkingSpotOccupation = getParkingSpotOccupation();
        if (parkingSpotOccupation.canHandle(vehicle.getVehicleSize())) {
            return announceFailure(new ParkingSpotEvent.ParkingSpotOccupationFailed(getParkingSpotId(), vehicle, "there is not enough space for vehicle"));
        }

        ParkingSpotEvent.ParkingSpotOccupied parkingSpotOccupied = new ParkingSpotEvent.ParkingSpotOccupied(getParkingSpotId(), vehicle);
        if (parkingSpotOccupation.occupyWith(vehicle.getVehicleSize()).isFull()) {
            return announceSuccess(ParkingSpotEvent.ParkingSpotOccupiedEvents.events(getParkingSpotId(), parkingSpotOccupied, new ParkingSpotEvent.FullyOccupied(getParkingSpotId())));
        }
        return announceSuccess(ParkingSpotEvent.ParkingSpotOccupiedEvents.events(getParkingSpotId(), parkingSpotOccupied));
    }

}
