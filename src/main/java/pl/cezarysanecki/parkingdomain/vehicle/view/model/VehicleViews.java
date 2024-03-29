package pl.cezarysanecki.parkingdomain.vehicle.view.model;

import pl.cezarysanecki.parkingdomain.vehicle.parking.application.RegisteringVehicle.VehicleRegistered;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleEvent.VehicleDroveAway;
import pl.cezarysanecki.parkingdomain.vehicle.parking.model.VehicleEvent.VehicleParked;

import java.util.Set;

public interface VehicleViews {

    Set<VehicleView> queryForNotParkedVehicles();

    Set<VehicleView> queryForParkedVehicles();

    Set<VehicleView> queryForAllVehicles();

    void handle(VehicleRegistered event);

    void handle(VehicleParked event);

    void handle(VehicleDroveAway event);

}
