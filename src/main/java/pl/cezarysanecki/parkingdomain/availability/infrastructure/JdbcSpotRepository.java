package pl.cezarysanecki.parkingdomain.availability.infrastructure;

import pl.cezarysanecki.parkingdomain.availability.model.Spot;
import pl.cezarysanecki.parkingdomain.availability.model.Spots;
import pl.cezarysanecki.parkingdomain.model.VehicleType;

import java.util.Optional;

class JdbcSpotRepository implements Spots {

    @Override
    public Optional<Spot.Assigned> findAvailableFor(final VehicleType vehicleType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Spot.Free> findAvailable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(final Spot spot) {
        throw new UnsupportedOperationException();
    }

}
