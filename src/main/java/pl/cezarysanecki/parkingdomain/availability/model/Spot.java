package pl.cezarysanecki.parkingdomain.availability.model;

import lombok.NonNull;
import lombok.Value;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

public interface Spot {

    ParkingSpotId getParkingSpotId();

    @Value
    class Free {

        @NonNull
        ParkingSpotId parkingSpotId;

        public Assigned assignTo(VehicleType vehicleType) {
            return new Assigned(parkingSpotId, vehicleType);
        }

    }

    @Value
    class Assigned {

        @NonNull
        ParkingSpotId parkingSpotId;
        @NonNull
        VehicleType vehicleType;

    }

}
