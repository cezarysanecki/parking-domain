package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.management.vehicle.VehicleId;
import pl.cezarysanecki.parkingdomain.parking.Vehicle;

public interface Vehicles {

    Option<Vehicle> findBy(VehicleId vehicleId);

    Vehicle publish(VehicleEvent vehicleEvent);

}
