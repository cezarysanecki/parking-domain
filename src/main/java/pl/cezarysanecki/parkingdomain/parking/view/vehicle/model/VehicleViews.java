package pl.cezarysanecki.parkingdomain.parking.view.vehicle.model;

import pl.cezarysanecki.parkingdomain.catalogue.vehicle.VehicleRegistered;

import java.util.Set;

import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleDroveAway;
import static pl.cezarysanecki.parkingdomain.parking.vehicle.model.VehicleEvent.VehicleParked;

public interface VehicleViews {

    Set<VehicleView> queryForNotParkedVehicles();

    Set<VehicleView> queryForParkedVehicles();

    Set<VehicleView> queryForAllVehicles();

    void handle(VehicleRegistered event);

    void handle(VehicleParked event);

    void handle(VehicleDroveAway event);

}
