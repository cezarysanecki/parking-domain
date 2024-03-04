package pl.cezarysanecki.parkingdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cezarysanecki.parkingdomain.model.Vehicle;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.repository.VehicleRepository;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Vehicle create(VehicleType vehicleType) {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(vehicleType);
        return vehicleRepository.save(vehicle);
    }

}
