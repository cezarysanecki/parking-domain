package pl.cezarysanecki.parkingdomain.parking.vehicle.model;

import io.vavr.control.Option;
import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleId;

public interface Vehicles {

    Option<Vehicle> findBy(VehicleId vehicleId);

    Vehicle publish(VehicleEvent vehicleEvent);

}
