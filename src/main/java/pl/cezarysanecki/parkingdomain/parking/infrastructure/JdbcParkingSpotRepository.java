package pl.cezarysanecki.parkingdomain.parking.infrastructure;

import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpots;

import java.util.Optional;

class JdbcParkingSpotRepository implements ParkingSpots {

    @Override
    public Optional<ParkingSpot> findBy(ParkingSpotId parkingSpotId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(ParkingSpot parkingSpot) {
        throw new UnsupportedOperationException();
    }

}
