package pl.cezarysanecki.parkingdomain.availability.model;

import pl.cezarysanecki.parkingdomain.model.VehicleType;

import java.util.Optional;

public interface Spots {

    Optional<Spot.Assigned> findAvailableFor(VehicleType vehicleType);

    Optional<Spot.Free> findAvailable();

    void save(Spot spot);

}
