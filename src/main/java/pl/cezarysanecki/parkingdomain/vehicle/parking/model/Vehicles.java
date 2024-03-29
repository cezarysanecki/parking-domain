package pl.cezarysanecki.parkingdomain.vehicle.parking.model;

import io.vavr.control.Option;

public interface Vehicles {

    Option<Vehicle> findBy(VehicleId vehicleId);

    Vehicle publish(VehicleEvent vehicleEvent);

}
