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
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
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

}
