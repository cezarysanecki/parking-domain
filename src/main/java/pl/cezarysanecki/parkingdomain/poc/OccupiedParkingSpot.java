package pl.cezarysanecki.parkingdomain.poc;

import java.util.List;

interface OccupiedParkingSpot extends ParkingSpot {

    List<VehicleId> parkedVehicles();

}
