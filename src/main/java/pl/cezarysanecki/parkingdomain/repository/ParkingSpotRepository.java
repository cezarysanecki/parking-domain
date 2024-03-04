package pl.cezarysanecki.parkingdomain.repository;

import org.springframework.data.repository.CrudRepository;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;

public interface ParkingSpotRepository extends CrudRepository<ParkingSpot, Long> {

    default ParkingSpot findBy(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalStateException("cannot find parking spot by id: " + id));
    }

}
