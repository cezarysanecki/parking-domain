package pl.cezarysanecki.parkingdomain.repository;

import org.springframework.data.repository.CrudRepository;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;

public interface ParkingSpotRepository extends CrudRepository<ParkingSpot, Long> {
}
