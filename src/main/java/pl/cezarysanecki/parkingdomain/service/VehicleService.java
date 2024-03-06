package pl.cezarysanecki.parkingdomain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cezarysanecki.parkingdomain.model.ParkedVehicleTypesOnParkingSpot;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.model.Vehicle;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.repository.ParkingSpotRepository;
import pl.cezarysanecki.parkingdomain.repository.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ParkingSpotService parkingSpotService;
    private final ParkingSpotRepository parkingSpotRepository;

    public Vehicle create(VehicleType vehicleType) {
        return vehicleRepository.save(Vehicle.createUsing(vehicleType));
    }

    @Transactional
    public Long park(Long parkingSpotId, Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotRepository.findBy(parkingSpotId);

        if (!parkingSpot.isCompletelyFree()) {
            ParkedVehicleTypesOnParkingSpot parkedVehicleTypes = parkingSpot.getParkedVehicleTypes();

            if (parkedVehicleTypes.isFullyOccupiedByCars()) {
                throw new IllegalStateException("Parking spot is already occupied by car");
            }
            if (parkedVehicleTypes.isFullyOccupiedByMotorcycles()) {
                throw new IllegalStateException("Parking spot is already occupied by 2 motorcycles");
            }
            if (parkedVehicleTypes.isFullyOccupiedByBikesOrScooters()) {
                throw new IllegalStateException("Parking spot is already occupied by 3 bikes or scooters");
            }

            if (!parkedVehicleTypes.contains(vehicle.getType())) {
                throw new IllegalStateException("Cannot mix vehicle types, this is for: " + parkedVehicleTypes.getVehicleTypes().get(0));
            }

            if (parkingSpot.isNotReservedFor(vehicle.getId())) {
                throw new IllegalStateException("cannot park on reserved parking spot");
            }
        }

        parkingSpot.assignVehicle(vehicle);

        return vehicle.getId();
    }

    @Transactional
    public Vehicle parkAnywhere(Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotService.findAnyAvailableFor(vehicle);

        if (parkingSpot.isCompletelyFree()) {
            parkingSpot.assignVehicle(vehicle);

            return vehicle;
        }

        if (parkingSpot.isOccupiedBy(vehicle.getId())) {
            return vehicle;
        }

        List<VehicleType> parkVehicleTypes = parkingSpot.getVehicleTypes();

        if (parkVehicleTypes.size() == 1 && parkVehicleTypes.get(0) == VehicleType.CAR) {
            throw new IllegalStateException("Parking spot is already occupied by car");
        }
        if (parkVehicleTypes.size() == 2 && parkVehicleTypes.stream().allMatch(type -> type == VehicleType.MOTORCYCLE)) {
            throw new IllegalStateException("Parking spot is already occupied by 2 motorcycles");
        }
        if (parkVehicleTypes.size() == 3 && parkVehicleTypes.stream().allMatch(type -> type == VehicleType.BIKE || type == VehicleType.SCOOTER)) {
            throw new IllegalStateException("Parking spot is already occupied by 3 bikes or scooters");
        }

        if (!parkVehicleTypes.contains(vehicle.getType())) {
            throw new IllegalStateException("Cannot mix vehicle types, this is for: " + parkVehicleTypes.get(0));
        }

        if (parkingSpot.isNotReservedFor(vehicle.getId())) {
            throw new IllegalStateException("cannot park on reserved parking spot");
        }

        parkingSpot.assignVehicle(vehicle);

        return vehicle;
    }

    public Vehicle findBy(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("cannot find vehicle by id: " + id));
    }

    public List<Vehicle> findAll() {
        Iterable<Vehicle> vehicles = vehicleRepository.findAll();
        return StreamSupport.stream(vehicles.spliterator(), false)
                .collect(Collectors.toList());
    }

}
