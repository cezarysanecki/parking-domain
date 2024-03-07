package pl.cezarysanecki.parkingdomain.availability.infrastructure;

import pl.cezarysanecki.parkingdomain.availability.model.Spot;
import pl.cezarysanecki.parkingdomain.availability.model.Spots;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.parking.model.ParkingSpotId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class InMemorySpotRepository implements Spots {

    private final Map<ParkingSpotId, Spot> database = new ConcurrentHashMap<>();

    @Override
    public Optional<Spot.Assigned> findAvailableFor(VehicleType vehicleType) {
        return database.values()
                .stream()
                .filter(Spot.Assigned.class::isInstance)
                .map(Spot.Assigned.class::cast)
                .filter(assigned -> assigned.getVehicleType() == vehicleType)
                .findFirst();
    }

    @Override
    public Optional<Spot.Free> findAvailable() {
        return database.values()
                .stream()
                .filter(Spot.Free.class::isInstance)
                .map(Spot.Free.class::cast)
                .findFirst();
    }

    @Override
    public void save(Spot spot) {
        database.put(spot.getParkingSpotId(), spot);
    }
}
