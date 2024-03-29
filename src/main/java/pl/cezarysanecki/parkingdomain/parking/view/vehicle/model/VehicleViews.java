package pl.cezarysanecki.parkingdomain.parking.view.vehicle.model;

import pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent;
import pl.cezarysanecki.parkingdomain.parking.vehicle.application.RegisteringVehicle.VehicleRegistered;

import java.util.Set;

public interface VehicleViews {

    Set<VehicleView> queryForNotParkedVehicles();

    Set<VehicleView> queryForParkedVehicles();

    Set<VehicleView> queryForAllVehicles();

    void handle(VehicleRegistered event);

    void handle(VehicleEvent.VehicleParked event);

    void handle(VehicleEvent.VehicleDroveAway event);

}
