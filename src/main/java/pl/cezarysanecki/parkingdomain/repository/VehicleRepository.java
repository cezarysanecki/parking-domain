package pl.cezarysanecki.parkingdomain.repository;

import org.springframework.data.repository.CrudRepository;
import pl.cezarysanecki.parkingdomain.model.Vehicle;

public interface VehicleRepository extends CrudRepository<Vehicle, Long> {
}
