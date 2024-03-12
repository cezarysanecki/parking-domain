package pl.cezarysanecki.parkingdomain.reservation.application;

import lombok.Value;
import pl.cezarysanecki.parkingdomain.GlobalConstants;
import pl.cezarysanecki.parkingdomain.parking.model.Vehicle;
import pl.cezarysanecki.parkingdomain.parking.model.VehicleSizeUnit;
import pl.cezarysanecki.parkingdomain.reservation.model.ReservationSlot;

import java.util.Set;

@Value
public class ReserveAnyParkingSpotCommand {

    Set<Vehicle> vehicles;
    ReservationSlot reservationSlot;

    public ReserveAnyParkingSpotCommand(Set<Vehicle> vehicles, ReservationSlot reservationSlot) {
        Integer requestedSpace = vehicles.stream()
                .map(Vehicle::getVehicleSizeUnit)
                .map(VehicleSizeUnit::getValue)
                .reduce(0, Integer::sum);
        if (requestedSpace > GlobalConstants.ParkingSlot.AVAILABLE_SPACE) {
            throw new IllegalArgumentException("parking spot cannot accommodate requested vehicles because of space");
        }

        this.vehicles = vehicles;
        this.reservationSlot = reservationSlot;
    }

}
