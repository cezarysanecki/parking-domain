package pl.cezarysanecki.parkingdomain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @ManyToOne
    @JsonBackReference
    private ParkingSpot parkingSpot;

    public static Vehicle createUsing(VehicleType vehicleType) {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(vehicleType);
        return vehicle;
    }

}
