package pl.cezarysanecki.parkingdomain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ParkingSpotStatus status;

    @OneToMany
    private List<Vehicle> vehicles;

    @OneToOne
    private Vehicle reservedBy;

    public ParkingSpot() {
        status = ParkingSpotStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return getStatus() == ParkingSpotStatus.AVAILABLE;
    }

    public boolean isFull() {
        List<VehicleType> parkVehicleTypes = getVehicles()
                .stream()
                .map(Vehicle::getType)
                .toList();

        return parkVehicleTypes.size() == 1 && parkVehicleTypes.get(0) == VehicleType.CAR
                || parkVehicleTypes.size() == 2 && parkVehicleTypes.stream().allMatch(type -> type == VehicleType.MOTORCYCLE)
                || parkVehicleTypes.size() == 3 && parkVehicleTypes.stream().allMatch(type -> type == VehicleType.BIKE || type == VehicleType.SCOOTER);
    }

    public void deleteReservation() {
        setReservedBy(null);
        if (getVehicles().isEmpty()) {
            setStatus(ParkingSpotStatus.AVAILABLE);
        } else {
            setStatus(ParkingSpotStatus.OCCUPIED);
        }
    }

    public void reserveFor(final Vehicle vehicle) {
        if (getReservedBy() != null && !getReservedBy().getId().equals(vehicle.getId())) {
            throw new IllegalStateException("cannot reserve reserved parking spot");
        }
        if (getStatus() != ParkingSpotStatus.AVAILABLE) {
            throw new IllegalStateException("cannot reserve unavailable parking spot");
        }

        setStatus(ParkingSpotStatus.RESERVED);
        setReservedBy(vehicle);
    }

    public boolean isTheSameType(VehicleType type) {
        List<VehicleType> parkVehicleTypes = getVehicles()
                .stream()
                .map(Vehicle::getType)
                .toList();

        return !parkVehicleTypes.isEmpty() && parkVehicleTypes.contains(type);
    }

    public boolean isAnotherPlaceFor(VehicleType type) {
        return !isFull() && isTheSameType(type);
    }
}
