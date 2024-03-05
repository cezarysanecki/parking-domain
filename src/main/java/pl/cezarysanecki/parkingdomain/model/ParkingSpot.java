package pl.cezarysanecki.parkingdomain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public boolean isAvailable() {
        return getStatus() == ParkingSpotStatus.AVAILABLE;
    }

    @JsonIgnore
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
        this.reservedBy = null;
        if (this.vehicles.isEmpty()) {
            this.status = ParkingSpotStatus.AVAILABLE;
        } else {
            this.status = ParkingSpotStatus.OCCUPIED;
        }
    }

    public void reserveFor(final Vehicle vehicle) {
        if (this.reservedBy != null && !this.reservedBy.getId().equals(vehicle.getId())) {
            throw new IllegalStateException("cannot reserve reserved parking spot");
        }
        if (!isAvailable()) {
            throw new IllegalStateException("cannot reserve unavailable parking spot");
        }

        this.status = ParkingSpotStatus.RESERVED;
        this.reservedBy = vehicle;
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

    public boolean isNotReservedFor(Long vehicleId) {
        return this.reservedBy != null && !this.reservedBy.getId().equals(vehicleId);
    }

    public List<VehicleType> getVehicleTypes() {
        return this.vehicles.stream()
                .map(Vehicle::getType)
                .toList();
    }
}
