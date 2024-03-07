package pl.cezarysanecki.parkingdomain.availability.model;

import pl.cezarysanecki.parkingdomain.model.VehicleType;

import java.util.Optional;

public interface AvailableSpots {

    Optional<AvailableSpot> findAvailableFor(VehicleType vehicleType);

    Optional<AvailableSpot> findAvailable();

    void save(AvailableSpot availableSpot);

}
